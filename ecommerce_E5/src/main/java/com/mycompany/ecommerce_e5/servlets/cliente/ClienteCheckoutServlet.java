/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.cliente;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.dominio.*;
import com.mycompany.ecommerce_e5.dominio.enums.MetodoPago;
import com.mycompany.ecommerce_e5.excepciones.StockInsuficienteException;
import com.mycompany.ecommerce_e5.util.ConfiguracionTienda;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet del proceso de checkout (finalizacion de compra).
 * Requiere que el usuario este logueado. Muestra el formulario para
 * ingresar la direccion de envio y seleccionar el metodo de pago.
 * Al confirmar, crea el pedido y limpia el carrito.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "ClienteCheckoutServlet", urlPatterns = {"/cliente/checkout"})
public class ClienteCheckoutServlet extends HttpServlet {

    private PedidoBO pedidoBO;
    private ProductoBO productoBO;

    @Override
    public void init() {
        pedidoBO = new PedidoBO();
        productoBO = new ProductoBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=cliente/checkout");
            return;
        }
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cliente/carrito");
            return;
        }

        double subtotal = 0;
        for (DetallePedido d : carrito) {
            subtotal += d.getPrecioUnitario() * d.getCantidad();
        }

        double costoEnvio = ConfiguracionTienda.calcularCostoEnvio(subtotal);

        request.setAttribute("subtotal", subtotal);
        request.setAttribute("costoEnvio", costoEnvio);
        request.setAttribute("total", subtotal + costoEnvio);
        request.setAttribute("metodosPago", MetodoPago.values());
        request.getRequestDispatcher("/cliente/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // Protección contra doble envío (#26)
        String submitToken = request.getParameter("submitToken");
        String sessionToken = (String) session.getAttribute("checkoutToken");
        if (submitToken != null && submitToken.equals(sessionToken)) {
            // Token ya usado, posible doble envío
            response.sendRedirect(request.getContextPath() + "/cliente/cuenta");
            return;
        }

        try {
            Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
            if (u == null) {
                response.sendRedirect(request.getContextPath() + "/login?redirect=cliente/checkout");
                return;
            }

            @SuppressWarnings("unchecked")
            List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
            if (carrito == null || carrito.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cliente/carrito");
                return;
            }

            // Validar campos de dirección (#24)
            String calle = request.getParameter("calle");
            String ciudad = request.getParameter("ciudad");
            String estado = request.getParameter("estado");
            String codigoPostal = request.getParameter("codigoPostal");

            if (calle == null || calle.trim().isEmpty()) {
                throw new Exception("La calle es requerida");
            }
            if (ciudad == null || ciudad.trim().isEmpty()) {
                throw new Exception("La ciudad es requerida");
            }
            if (estado == null || estado.trim().isEmpty()) {
                throw new Exception("El estado es requerido");
            }
            if (codigoPostal == null || codigoPostal.trim().isEmpty()) {
                throw new Exception("El código postal es requerido");
            }
            if (!codigoPostal.matches("\\d{5}")) {
                throw new Exception("El código postal debe tener 5 dígitos");
            }

            // Validar stock disponible antes de crear pedido (#25)
            List<String> erroresStock = new ArrayList<>();
            for (DetallePedido detalle : carrito) {
                try {
                    productoBO.validarStock(detalle.getProducto().getId(), detalle.getCantidad());
                } catch (StockInsuficienteException e) {
                    erroresStock.add(detalle.getProducto().getNombre() + ": " + e.getMessage());
                }
            }
            if (!erroresStock.isEmpty()) {
                throw new Exception("Stock insuficiente para: " + String.join(", ", erroresStock) +
                        ". Por favor, actualiza las cantidades en tu carrito.");
            }

            Direccion dir = new Direccion();
            dir.setCalle(calle.trim());
            dir.setCiudad(ciudad.trim());
            dir.setEstado(estado.trim());
            dir.setCodigoPostal(codigoPostal.trim());
            dir.setUsuario(u);

            MetodoPago mp = MetodoPago.valueOf(request.getParameter("metodoPago"));
            Pedido pedido = pedidoBO.crearPedido(u, dir, carrito, mp);

            // Marcar token como usado para prevenir doble envío
            if (submitToken != null) {
                session.setAttribute("checkoutToken", submitToken);
            }

            session.removeAttribute("carrito");
            response.sendRedirect(request.getContextPath() + "/cliente/confirmacion?id=" + pedido.getId());

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Checkout Cliente";
    }
}
