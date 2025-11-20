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
@WebServlet(name = "LoginServlet", urlPatterns = {"/admin/login"})
public class LoginServlet extends HttpServlet {

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
            // Dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        try {
            // Validar credenciales
            Usuario usuario = usuarioBO.login(correo, contrasena);

            // Verificar que sea admin placoso
            if (!usuarioBO.esAdministrador(usuario)) {
                request.setAttribute("error", "Acceso denegado. Solo administradores pueden ingresar.");
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
                return;
            }

            // Crear sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("esAdmin", true);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setMaxInactiveInterval(30 * 60); // 30 minutos

            // Dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");

        } catch (Exception e) {
            // Error en login
            request.setAttribute("error", e.getMessage());
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet de Login para Administradores";
    }
}
