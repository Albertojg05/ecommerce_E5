/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.admin;

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
 * Servlet para la gestion de pedidos desde el panel de administracion.
 * Permite listar pedidos, ver detalles, filtrar por estado,
 * actualizar el estado de un pedido y cancelarlo.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "AdminPedidoServlet", urlPatterns = {"/admin/pedidos"})
public class PedidoServlet extends HttpServlet {

    private PedidoBO pedidoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        try {
            if (accion == null || accion.equals("listar")) {
                listarPedidos(request, response);
            } else if (accion.equals("detalle")) {
                verDetallePedido(request, response);
            } else if (accion.equals("filtrar")) {
                filtrarPorEstado(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/pedidos.jsp").forward(request, response);
        }
    }

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

    private void listarPedidos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Pedido> pedidos = pedidoBO.obtenerTodos();
        request.setAttribute("pedidos", pedidos);
        request.setAttribute("estados", EstadoPedido.values());
        request.getRequestDispatcher("/admin/pedidos.jsp").forward(request, response);
    }

    private void verDetallePedido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {

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

    private void actualizarEstadoPedido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String nuevoEstadoStr = request.getParameter("nuevoEstado");

        try {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr);
            pedidoBO.actualizarEstado(id, nuevoEstado);

            response.sendRedirect(request.getContextPath() +
                    "/admin/pedidos?accion=detalle&id=" + id + "&success=updated");
        } catch (Exception e) {
            // Si hay error, volver al detalle del pedido con el mensaje de error
            try {
                Pedido pedido = pedidoBO.obtenerPorId(id);
                request.setAttribute("pedido", pedido);
                request.setAttribute("estados", EstadoPedido.values());
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/admin/pedido-detalle.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/admin/pedidos");
            }
        }
    }

    private void cancelarPedido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        try {
            pedidoBO.cancelarPedido(id);
            response.sendRedirect(request.getContextPath() +
                    "/admin/pedidos?accion=detalle&id=" + id + "&success=cancelled");
        } catch (Exception e) {
            // Si hay error, volver al detalle del pedido con el mensaje de error
            try {
                Pedido pedido = pedidoBO.obtenerPorId(id);
                request.setAttribute("pedido", pedido);
                request.setAttribute("estados", EstadoPedido.values());
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/admin/pedido-detalle.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/admin/pedidos");
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet de Gestión de Pedidos";
    }
}
