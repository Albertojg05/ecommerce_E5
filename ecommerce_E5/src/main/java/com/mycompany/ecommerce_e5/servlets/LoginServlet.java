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
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

@WebServlet(name = "LoginServlet", urlPatterns = {"/admin/login"})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    
    private UsuarioBO usuarioBO;
    
    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
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
    
    
    //PAra mostrar formulario de login
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar si ya hay activa
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            // redirigir al dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        // Mostrar formulario de login
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    
    
    //ARREGLAR DOCUMENTACION
    
    //Procesaer el login
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener parámetros del formulario
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        
        try {
            // Validar credenciales
            Usuario usuario = usuarioBO.login(correo, contrasena);
            
            // Verificar que sea administrador
            if (!usuarioBO.esAdministrador(usuario)) {
                request.setAttribute("error", "Acceso denegado. Solo administradores pueden ingresar.");
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
                return;
            }
            
            // Crear sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("esAdmin", true);
            session.setMaxInactiveInterval(30 * 60); // 30 minutos
            
            // Redireccionar al dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            
        } catch (Exception e) {
            // Error en login
            request.setAttribute("error", e.getMessage());
            request.setAttribute("correo", correo); // Mantener el correo ingresado
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de Login para Administradores";
    }
}
