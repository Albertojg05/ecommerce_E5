/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package com.mycompany.ecommerce_e5.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de autenticacion para rutas de cliente que requieren login.
 * Protege las paginas de checkout, cuenta y confirmacion.
 * Permite ver productos y carrito sin estar logueado.
 * Si no hay sesion, guarda la URL original y redirige al login.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebFilter(filterName = "ClienteAuthFilter", urlPatterns = {
    "/cliente/checkout",
    "/cliente/cuenta",
    "/cliente/confirmacion"
})
public class ClienteAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            // Guardar la URL original para redireccionar después del login
            String requestURI = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            String redirectURL = requestURI.substring(httpRequest.getContextPath().length() + 1);

            if (queryString != null) {
                redirectURL += "?" + queryString;
            }

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?redirect=" + redirectURL);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
