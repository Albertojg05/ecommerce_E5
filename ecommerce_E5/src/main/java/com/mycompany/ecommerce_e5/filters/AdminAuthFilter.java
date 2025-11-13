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
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización si es necesaria
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Obtener la URI de la petición
        String requestURI = httpRequest.getRequestURI();
        
        // Permitir acceso a login sin autenticación
        if (requestURI.endsWith("/admin/login")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si hay sesión activa
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            // No hay sesión, redirigir al login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login");
            return;
        }
        
        // Verificar que sea administrador
        Boolean esAdmin = (Boolean) session.getAttribute("esAdmin");
        
        if (esAdmin == null || !esAdmin) {
            // No es administrador, denegar acceso
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Aceso denegado. Solo administradores."); //Cambiar??
            return;
        }
        
        // Todo chilo, continuar con la 
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Limpieza por si acaso.
    }
}
