/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.CategoriaBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.dominio.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

@WebServlet(name = "ProductoServlet", urlPatterns = {"/admin/productos"})
public class ProductoServlet extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;
    
    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion == null || accion.equals("listar")) {
                // Listar todos los productos
                listarProductos(request, response);
                
            } else if (accion.equals("nuevo")) {
                // Mostrar formulario para crear nuevo producto
                mostrarFormularioNuevo(request, response);
                
            } else if (accion.equals("editar")) {
                // Mostrar formulario para editar producto
                mostrarFormularioEditar(request, response);
                
            } else if (accion.equals("eliminar")) {
                // Eliminar producto
                eliminarProducto(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/productos.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion.equals("crear")) {
                crearProducto(request, response);
            } else if (accion.equals("actualizar")) {
                actualizarProducto(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/productos.jsp").forward(request, response);
        }
    }
    
    /**
     * Listar todos los productos
     */
    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Producto> productos = productoBO.obtenerTodos();
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/admin/productos.jsp").forward(request, response);
    }
    
    /**
     * Mostrar formulario para nuevo producto
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("/admin/producto-form.jsp").forward(request, response);
    }
    
    /**
     * Mostrar formulario para editar producto
     */
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Producto producto = productoBO.obtenerPorId(id);
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        
        request.setAttribute("producto", producto);
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("/admin/producto-form.jsp").forward(request, response);
    }
    
    /**
     * Crear nuevo producto
     */
    private void crearProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        // Obtener parámetros del formulario
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        double precio = Double.parseDouble(request.getParameter("precio"));
        String imagenUrl = request.getParameter("imagenUrl");
        int existencias = Integer.parseInt(request.getParameter("existencias"));
        String talla = request.getParameter("talla");
        String color = request.getParameter("color");
        int categoriaId = Integer.parseInt(request.getParameter("categoriaId"));
        
        // Obtener la categoría
        Categoria categoria = categoriaBO.obtenerPorId(categoriaId);
        
        // Crear el producto
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setImagenUrl(imagenUrl);
        producto.setExistencias(existencias);
        producto.setTalla(talla);
        producto.setColor(color);
        producto.setCategoria(categoria);
        
        // Guardar
        productoBO.crear(producto);
        
        // Redireccionar con mensaje de éxito
        response.sendRedirect(request.getContextPath() + "/admin/productos?success=created");
    }
    
    /**
     * Actualizar producto existente
     */
    private void actualizarProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        // Obtener parámetros del formulario
        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        double precio = Double.parseDouble(request.getParameter("precio"));
        String imagenUrl = request.getParameter("imagenUrl");
        int existencias = Integer.parseInt(request.getParameter("existencias"));
        String talla = request.getParameter("talla");
        String color = request.getParameter("color");
        int categoriaId = Integer.parseInt(request.getParameter("categoriaId"));
        
        // Obtener la categoría
        Categoria categoria = categoriaBO.obtenerPorId(categoriaId);
        
        // Obtener el producto y actualizarlo
        Producto producto = productoBO.obtenerPorId(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setImagenUrl(imagenUrl);
        producto.setExistencias(existencias);
        producto.setTalla(talla);
        producto.setColor(color);
        producto.setCategoria(categoria);
        
        // Actualizar
        productoBO.actualizar(producto);
        
        // Redireccionar con mensaje de éxito
        response.sendRedirect(request.getContextPath() + "/admin/productos?success=updated");
    }
    
    /**
     * Eliminar producto
     */
    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        int id = Integer.parseInt(request.getParameter("id"));
        productoBO.eliminar(id);
        
        // Redireccionar con mensaje de éxito
        response.sendRedirect(request.getContextPath() + "/admin/productos?success=deleted");
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de Gestión de Productos";
    }
}
