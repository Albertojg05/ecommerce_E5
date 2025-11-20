/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.dominio.*;
import com.mycompany.ecommerce_e5.dominio.enums.MetodoPago;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {
    
    private PedidoBO pedidoBO;
    
    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return;
        }
        
        // Verificar si hay productos en el carrito
        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrito");
            return;
        }
        
        double subtotal = 0;
        for (DetallePedido detalle : carrito) {
            subtotal += detalle.getPrecioUnitario() * detalle.getCantidad();
        }
        
        double costoEnvio = 200.0;
        double total = subtotal + costoEnvio;
        
        request.setAttribute("usuario", usuario);
        request.setAttribute("carrito", carrito);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("costoEnvio", costoEnvio);
        request.setAttribute("total", total);
        request.setAttribute("metodosPago", MetodoPago.values());
        
        request.getRequestDispatcher("/checkout.html").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        
        try {
            // Obtener usuario
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario == null) {
                throw new Exception("Debe iniciar sesión para realizar la compra");
            }
            
            // Obtener carrito
            @SuppressWarnings("unchecked")
            List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
            
            if (carrito == null || carrito.isEmpty()) {
                throw new Exception("El carrito está vacío");
            }
            
            // Obtener datos de dirección
            String calle = request.getParameter("calle");
            String ciudad = request.getParameter("ciudad");
            String estado = request.getParameter("estado");
            String codigoPostal = request.getParameter("codigoPostal");
            
            // Crear dirección de envío
            Direccion direccion = new Direccion();
            direccion.setCalle(calle);
            direccion.setCiudad(ciudad);
            direccion.setEstado(estado);
            direccion.setCodigoPostal(codigoPostal);
            direccion.setUsuario(usuario);
            
            // Obtener método de pago
            String metodoPagoStr = request.getParameter("metodoPago");
            MetodoPago metodoPago = MetodoPago.valueOf(metodoPagoStr);
            
            // Crear pedido
            Pedido pedido = pedidoBO.crearPedido(usuario, direccion, carrito, metodoPago);
            
            // Limpiar carrito
            session.removeAttribute("carrito");
            
            // Redirigir a confirmación
            response.sendRedirect(request.getContextPath() + 
                    "/confirm?pedidoId=" + pedido.getId());
            
        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar el pedido: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de proceso de checkout";
    }
}
