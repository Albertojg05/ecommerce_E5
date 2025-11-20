/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.UsuarioBO;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.dominio.enums.RolUsuario;
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
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    
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
        
        // Verificar si ya hay sesión activa
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        request.getRequestDispatcher("/register.html").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String contrasena = request.getParameter("contrasena");
            String contrasenaConfirm = request.getParameter("contrasenaConfirm");
            String telefono = request.getParameter("telefono");
            
            // Validaciones 
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new Exception("El nombre es requerido");
            }
            if (correo == null || correo.trim().isEmpty()) {
                throw new Exception("El correo es requerido");
            }
            if (contrasena == null || contrasena.trim().isEmpty()) {
                throw new Exception("La contraseña es requerida");
            }
            if (contrasena.length() < 6) {
                throw new Exception("La contraseña debe tener al menos 6 caracteres");
            }
            if (!contrasena.equals(contrasenaConfirm)) {
                throw new Exception("Las contraseñas no coinciden");
            }
            
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setContrasena(contrasena); // Será hasheada en UsuarioBO
            usuario.setTelefono(telefono);
            usuario.setRol(RolUsuario.CLIENTE);
            
            // Registrar
            Usuario usuarioRegistrado = usuarioBO.registrar(usuario);
            
            // Crear sesión automáticamente
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuarioRegistrado);
            session.setAttribute("usuarioId", usuarioRegistrado.getId());
            session.setAttribute("usuarioNombre", usuarioRegistrado.getNombre());
            session.setAttribute("esAdmin", false);
            session.setMaxInactiveInterval(60 * 60); // 1 hora
            
            // Redirigir al inicio
            response.sendRedirect(request.getContextPath() + "/?registered=true");
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            
            // Mantener los datos ingresados (excepto contraseña)
            request.setAttribute("nombre", request.getParameter("nombre"));
            request.setAttribute("correo", request.getParameter("correo"));
            request.setAttribute("telefono", request.getParameter("telefono"));
            
            request.getRequestDispatcher("/register.html").forward(request, response);
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de registro de usuarios";
    }
}