/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.cliente;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.dominio.Pedido;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Servlet de confirmacion de pedido.
 * Muestra los detalles del pedido despues de completar la compra.
 * Verifica que el pedido pertenezca al usuario que esta logueado.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "ClienteConfirmacionServlet", urlPatterns = {"/cliente/confirmacion"})
public class ClienteConfirmacionServlet extends HttpServlet {

    private PedidoBO pedidoBO;

    @Override
    public void init() {
        pedidoBO = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String idParam = request.getParameter("id");

            // Validar que se proporcione ID (#30)
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "No se especificó el número de pedido");
                request.getRequestDispatcher("/cliente/confirm.jsp").forward(request, response);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Número de pedido inválido");
                request.getRequestDispatcher("/cliente/confirm.jsp").forward(request, response);
                return;
            }

            Pedido pedido = pedidoBO.obtenerPorId(id);

            // Validar que el pedido exista (#30)
            if (pedido == null) {
                request.setAttribute("error", "El pedido solicitado no existe");
                request.getRequestDispatcher("/cliente/confirm.jsp").forward(request, response);
                return;
            }

            // Validar propiedad del pedido (#29)
            if (pedido.getUsuario().getId() != usuario.getId()) {
                request.setAttribute("error", "No tienes permiso para ver este pedido");
                request.getRequestDispatcher("/cliente/confirm.jsp").forward(request, response);
                return;
            }

            request.setAttribute("pedido", pedido);
            request.getRequestDispatcher("/cliente/confirm.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el pedido: " + e.getMessage());
            request.getRequestDispatcher("/cliente/confirm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "Confirmación Cliente";
    }
}
