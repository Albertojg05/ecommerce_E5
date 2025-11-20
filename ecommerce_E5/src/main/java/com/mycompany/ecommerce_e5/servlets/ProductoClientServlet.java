/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.CategoriaBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.bo.ResenaBO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.dominio.Resena;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595 
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

@WebServlet(name = "ProductoClientServlet", urlPatterns = {"/productos"})
public class ProductoClientServlet extends HttpServlet {

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
            request.getRequestDispatcher("/product.html").forward(request, response);
        }
    }

    /**
     * Listar todos los productos con paginación
     */
    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        int pagina = 1;
        String paginaParam = request.getParameter("pagina");
        if (paginaParam != null) {
            try {
                pagina = Integer.parseInt(paginaParam);
            } catch (NumberFormatException e) {
                pagina = 1;
            }
        }

        int productosPorPagina = 12;
        
        List<Producto> productos = productoBO.obtenerTodos();
        int totalProductos = productos.size();
        int totalPaginas = (int) Math.ceil((double) totalProductos / productosPorPagina);
        int inicio = (pagina - 1) * productosPorPagina;
        int fin = Math.min(inicio + productosPorPagina, totalProductos);
        List<Producto> productosPagina = productos.subList(inicio, fin);
        List<Categoria> categorias = categoriaBO.obtenerTodas();

        request.setAttribute("productos", productosPagina);
        request.setAttribute("categorias", categorias);
        request.setAttribute("paginaActual", pagina);
        request.setAttribute("totalPaginas", totalPaginas);
        request.setAttribute("totalProductos", totalProductos);

        request.getRequestDispatcher("/product.html").forward(request, response);
    }

    /**
     * Ver detalle de un producto específico
     */
    private void verDetalleProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        Producto producto = productoBO.obtenerPorId(id);

        if (producto == null) {
            request.setAttribute("error", "Producto no encontrado");
            listarProductos(request, response);
            return;
        }

        // reseñas del producto
        List<Resena> resenas = resenaBO.obtenerPorProducto(id);

        // Calcular calificación promedio
        double promedioCalificacion = resenaBO.calcularPromedioProducto(id);

        request.setAttribute("producto", producto);
        request.setAttribute("resenas", resenas);
        request.setAttribute("promedioCalificacion", promedioCalificacion);
        request.setAttribute("totalResenas", resenas.size());

        request.getRequestDispatcher("/product-detail.html").forward(request, response);
    }

    /**
     * Buscar productos por nombre
     */
    private void buscarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String termino = request.getParameter("q");

        if (termino == null || termino.trim().isEmpty()) {
            listarProductos(request, response);
            return;
        }

        List<Producto> productos = productoBO.buscarPorNombre(termino);
        List<Categoria> categorias = categoriaBO.obtenerTodas();

        request.setAttribute("productos", productos);
        request.setAttribute("categorias", categorias);
        request.setAttribute("terminoBusqueda", termino);
        request.setAttribute("totalProductos", productos.size());

        request.getRequestDispatcher("/product.html").forward(request, response);
    }

    /**
     * Listar productos por categoría
     */
    private void listarPorCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoriaId = Integer.parseInt(request.getParameter("id"));

        List<Producto> productos = productoBO.obtenerPorCategoria(categoriaId);
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        Categoria categoriaSeleccionada = categoriaBO.obtenerPorId(categoriaId);

        request.setAttribute("productos", productos);
        request.setAttribute("categorias", categorias);
        request.setAttribute("categoriaSeleccionada", categoriaSeleccionada);
        request.setAttribute("totalProductos", productos.size());

        request.getRequestDispatcher("/product.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet de productos para clientes";
    }
}
