/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.dominio.DetallePedido;
import com.mycompany.ecommerce_e5.dominio.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
@WebServlet(name = "CarritoServlet", urlPatterns = {"/carrito"})
public class CarritoServlet extends HttpServlet {
    
    private ProductoBO productoBO;
    
    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        
        double subtotal = 0;
        int cantidadItems = 0;
        
        for (DetallePedido detalle : carrito) {
            subtotal += detalle.getPrecioUnitario() * detalle.getCantidad();
            cantidadItems += detalle.getCantidad();
        }
        
        double costoEnvio = 200.0; // Costo fijo de envío
        double total = subtotal + costoEnvio;
        
        request.setAttribute("carrito", carrito);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("costoEnvio", costoEnvio);
        request.setAttribute("total", total);
        request.setAttribute("cantidadItems", cantidadItems);
        
        request.getRequestDispatcher("/carrito.html").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();
        
        try {
            if (accion.equals("agregar")) {
                agregarAlCarrito(request, session);
            } else if (accion.equals("actualizar")) {
                actualizarCantidad(request, session);
            } else if (accion.equals("eliminar")) {
                eliminarDelCarrito(request, session);
            } else if (accion.equals("vaciar")) {
                vaciarCarrito(session);
            }
            
            
            response.sendRedirect(request.getContextPath() + "/carrito");
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Agregar producto al carrito
     */
    private void agregarAlCarrito(HttpServletRequest request, HttpSession session) throws Exception {
        int productoId = Integer.parseInt(request.getParameter("productoId"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero");
        }
        
        Producto producto = productoBO.obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("Producto no encontrado");
        }
        
        // Verificar stock
        if (producto.getExistencias() < cantidad) {
            throw new Exception("No hay suficiente stock disponible");
        }
        
        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        
        // Verificar si el producto ya está en el carrito
        boolean encontrado = false;
        for (DetallePedido detalle : carrito) {
            if (detalle.getProducto().getId() == productoId) {
                // Actualizar cantidad
                int nuevaCantidad = detalle.getCantidad() + cantidad;
                if (producto.getExistencias() < nuevaCantidad) {
                    throw new Exception("No hay suficiente stock disponible");
                }
                detalle.setCantidad(nuevaCantidad);
                encontrado = true;
                break;
            }
        }
        
        // Si no está en el carrito, agregarlo
        if (!encontrado) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            carrito.add(detalle);
        }
        
        session.setAttribute("carrito", carrito);
    }
    
    /**
     * Actualizar cantidad de un producto en el carrito
     */
    private void actualizarCantidad(HttpServletRequest request, HttpSession session) throws Exception {
        int productoId = Integer.parseInt(request.getParameter("productoId"));
        int nuevaCantidad = Integer.parseInt(request.getParameter("cantidad"));
        
        if (nuevaCantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero");
        }
        
        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        
        if (carrito != null) {
            for (DetallePedido detalle : carrito) {
                if (detalle.getProducto().getId() == productoId) {
                    // Verificar stock
                    Producto producto = productoBO.obtenerPorId(productoId);
                    if (producto.getExistencias() < nuevaCantidad) {
                        throw new Exception("No hay suficiente stock disponible");
                    }
                    detalle.setCantidad(nuevaCantidad);
                    break;
                }
            }
            session.setAttribute("carrito", carrito);
        }
    }
    
    /**
     * Eliminar producto del carrito
     */
    private void eliminarDelCarrito(HttpServletRequest request, HttpSession session) {
        int productoId = Integer.parseInt(request.getParameter("productoId"));
        
        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        
        if (carrito != null) {
            carrito.removeIf(detalle -> detalle.getProducto().getId() == productoId);
            session.setAttribute("carrito", carrito);
        }
    }
    
    /**
     * Vaciar todo el carrito
     */
    private void vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de gestión del carrito de compras";
    }
}
