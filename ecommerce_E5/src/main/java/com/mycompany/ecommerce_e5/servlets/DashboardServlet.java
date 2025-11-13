/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.bo.ResenaBO;
import com.mycompany.ecommerce_e5.dominio.Pedido;
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
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    
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
    private PedidoBO pedidoBO;
    private ResenaBO resenaBO;
    
    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        pedidoBO = new PedidoBO();
        resenaBO = new ResenaBO();
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
        
        try {
            // Obtener datos para el dashboard
            List<Producto> productos = productoBO.obtenerTodos();
            List<Pedido> pedidos = pedidoBO.obtenerTodos();
            List<Resena> resenas = resenaBO.obtenerTodas();
            
            // Calcular estadísticas
            int totalProductos = productos.size();
            int totalPedidos = pedidos.size();
            int totalResenas = resenas.size();
            
            // Productos con bajo stock (menos de 10)
            long productosStockBajo = productos.stream()
                    .filter(p -> p.getExistencias() < 10)
                    .count();
            
            // Pasar datos a la vista
            request.setAttribute("totalProductos", totalProductos);
            request.setAttribute("totalPedidos", totalPedidos);
            request.setAttribute("totalResenas", totalResenas);
            request.setAttribute("productosStockBajo", productosStockBajo);
            
            // Pedidos recientes (últimos 5)
            List<Pedido> pedidosRecientes = pedidos.subList(0, Math.min(5, pedidos.size()));
            request.setAttribute("pedidosRecientes", pedidosRecientes);
            
            // Forward a la vista JSP
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
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
        doGet(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet del Dashboard de Administrador";
    }
}
