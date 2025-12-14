/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.cliente;

import com.mycompany.ecommerce_e5.bo.CategoriaBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.bo.ResenaBO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.dominio.Resena;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Servlet del catalogo de productos para clientes.
 * Permite listar todos los productos, filtrar por categoria, buscar por nombre
 * y ver el detalle de cada producto con sus resenas.
 * Los clientes logueados pueden agregar resenas a los productos.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "ClienteProductoServlet", urlPatterns = {"/cliente/productos"})
public class ClienteProductoServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;
    private ResenaBO resenaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
        resenaBO = new ResenaBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");

        try {
            if (accion == null || accion.equals("listar")) {
                listarProductos(request, response);
            } else if (accion.equals("detalle")) {
                verDetalleProducto(request, response);
            } else if (accion.equals("buscar")) {
                buscarProductos(request, response);
            } else if (accion.equals("categoria")) {
                listarPorCategoria(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            listarProductos(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("agregarResena".equals(accion)) {
            agregarResena(request, response);
        } else {
            doGet(request, response);
        }
    }

    private void agregarResena(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int productoId = Integer.parseInt(request.getParameter("productoId"));
            int calificacion = Integer.parseInt(request.getParameter("calificacion"));
            String comentario = request.getParameter("comentario");

            Producto producto = productoBO.obtenerPorId(productoId);

            Resena resena = new Resena();
            resena.setCalificacion(calificacion);
            resena.setComentario(comentario);
            resena.setFecha(new Date());
            resena.setUsuario(usuario);
            resena.setProducto(producto);

            resenaBO.crear(resena);

            response.sendRedirect(request.getContextPath() + "/cliente/productos?accion=detalle&id=" + productoId + "&mensaje=resenaAgregada");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Producto> productos = productoBO.obtenerTodos();
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        request.setAttribute("productos", productos);
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("/cliente/productos.jsp").forward(request, response);
    }

    private void verDetalleProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID de producto no especificado");
                listarProductos(request, response);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID de producto inválido");
                listarProductos(request, response);
                return;
            }

            Producto producto = productoBO.obtenerPorId(id);
            if (producto == null) {
                request.setAttribute("error", "El producto solicitado no existe o ya no está disponible");
                listarProductos(request, response);
                return;
            }

            List<Resena> resenas = resenaBO.obtenerPorProducto(id);
            double promedio = resenaBO.calcularPromedioProducto(id);
            request.setAttribute("producto", producto);
            request.setAttribute("resenas", resenas);
            request.setAttribute("promedioCalificacion", promedio);
            request.setAttribute("totalResenas", resenas.size());
            request.getRequestDispatcher("/cliente/detalle-producto.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el producto: " + e.getMessage());
            listarProductos(request, response);
        }
    }

    private void buscarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String termino = request.getParameter("q");
        List<Producto> productos = productoBO.buscarPorNombre(termino);
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        request.setAttribute("productos", productos);
        request.setAttribute("categorias", categorias);
        request.setAttribute("terminoBusqueda", termino);
        request.getRequestDispatcher("/cliente/productos.jsp").forward(request, response);
    }

    private void listarPorCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        List<Producto> productos = productoBO.obtenerPorCategoria(id);
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        request.setAttribute("productos", productos);
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("/cliente/productos.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Catálogo Cliente";
    }
}
