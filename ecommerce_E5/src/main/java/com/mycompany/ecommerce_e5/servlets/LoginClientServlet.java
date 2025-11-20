/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.UsuarioBO;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
@WebServlet(name = "LoginClientServlet", urlPatterns = {"/login"})
public class LoginClientServlet extends HttpServlet {
    
    private UsuarioBO usuarioBO;
    
    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String redirect = request.getParameter("redirect");
        if (redirect != null) {
            request.setAttribute("redirect", redirect);
        }
        
        request.getRequestDispatcher("/login.html").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String redirect = request.getParameter("redirect");
        
        try {
            // Validar credenciales
            Usuario usuario = usuarioBO.login(correo, contrasena);
            
            // Crear sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("esAdmin", usuarioBO.esAdministrador(usuario));
            session.setMaxInactiveInterval(60 * 60); // 1 hora
            
            if (redirect != null && !redirect.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/" + redirect);
            } else if (usuarioBO.esAdministrador(usuario)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("correo", correo);
            if (redirect != null) {
                request.setAttribute("redirect", redirect);
            }
            request.getRequestDispatcher("/login.html").forward(request, response);
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de login para clientes";
    }
}
