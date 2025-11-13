/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.dominio.Pedido;
import com.mycompany.ecommerce_e5.dominio.enums.EstadoPedido;
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

@WebServlet(name = "PedidoServlet", urlPatterns = {"/admin/pedidos"})
public class PedidoServlet extends HttpServlet {
    
    private PedidoBO pedidoBO;
    
    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
    }

    //Listar pedidos o ver detalle
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion == null || accion.equals("listar")) {
                // Listar todos los pedidos
                listarPedidos(request, response);
                
            } else if (accion.equals("detalle")) {
                // Ver detalle de un pedido
                verDetallePedido(request, response);
                
            } else if (accion.equals("filtrar")) {
                // Filtrar por estado
                filtrarPorEstado(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/pedidos.jsp").forward(request, response);
        }
    }

    //Actualizar estado del pedido
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion.equals("actualizarEstado")) {
                actualizarEstadoPedido(request, response);
            } else if (accion.equals("cancelar")) {
                cancelarPedido(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/pedidos.jsp").forward(request, response);
        }
    }
    
    /**
     * Listar todos los pedidos
     */
    private void listarPedidos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Pedido> pedidos = pedidoBO.obtenerTodos();
        request.setAttribute("pedidos", pedidos);
        request.setAttribute("estados", EstadoPedido.values());
        request.getRequestDispatcher("/admin/pedidos.jsp").forward(request, response);
    }
    
    /**
     * Ver detalle de un pedido específico
     */
    private void verDetallePedido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Pedido pedido = pedidoBO.obtenerPorId(id);
        
        if (pedido == null) {
            request.setAttribute("error", "Pedido no encontrado");
            listarPedidos(request, response);
            return;
        }
        
        request.setAttribute("pedido", pedido);
        request.setAttribute("estados", EstadoPedido.values());
        request.getRequestDispatcher("/admin/pedido-detalle.jsp").forward(request, response);
    }
    
    /**
     * Filtrar pedidos por estado
     */
    private void filtrarPorEstado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String estadoStr = request.getParameter("estado");
        
        if (estadoStr == null || estadoStr.isEmpty() || estadoStr.equals("TODOS")) {
            listarPedidos(request, response);
            return;
        }
        
        EstadoPedido estado = EstadoPedido.valueOf(estadoStr);
        List<Pedido> pedidos = pedidoBO.obtenerPorEstado(estado);
        
        request.setAttribute("pedidos", pedidos);
        request.setAttribute("estados", EstadoPedido.values());
        request.setAttribute("estadoSeleccionado", estadoStr);
        request.getRequestDispatcher("/admin/pedidos.jsp").forward(request, response);
    }
    
    /**
     * Actualizar estado del pedido
     */
    private void actualizarEstadoPedido(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String nuevoEstadoStr = request.getParameter("nuevoEstado");
        
        EstadoPedido nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr);
        pedidoBO.actualizarEstado(id, nuevoEstado);
        
        // Redireccionar al detalle con mensaje de éxito
        response.sendRedirect(request.getContextPath() + 
                "/admin/pedidos?accion=detalle&id=" + id + "&success=updated");
    }
    
    /**
     * Cancelar pedido
     */
    private void cancelarPedido(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        int id = Integer.parseInt(request.getParameter("id"));
        pedidoBO.cancelarPedido(id);
        
        // Redireccionar con mensaje de éxito
        response.sendRedirect(request.getContextPath() + 
                "/admin/pedidos?accion=detalle&id=" + id + "&success=cancelled");
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de Gestión de Pedidos";
    }
}
