/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.ResenaBO;
import com.mycompany.ecommerce_e5.dominio.Resena;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

@WebServlet(name = "ResenaServlet", urlPatterns = {"/admin/resenas"})
public class ResenaServlet extends HttpServlet {
    
    private ResenaBO resenaBO;
    
    @Override
    public void init() throws ServletException {
        resenaBO = new ResenaBO();
    }

    //Listar reseñas
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion == null || accion.equals("listar")) {
                // Listar todas las reseñas
                listarResenas(request, response);
                
            } else if (accion.equals("porProducto")) {
                // Listar reseñas de un producto específico
                listarPorProducto(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/resenas.jsp").forward(request, response);
        }
    }

    //Eliminar reseña
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion.equals("eliminar")) {
                eliminarResena(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            listarResenas(request, response);
        }
    }
    
    /**
     * Listar todas las reseñaas
     */
    private void listarResenas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Resena> resenas = resenaBO.obtenerTodas();
        request.setAttribute("resenas", resenas);
        request.getRequestDispatcher("/admin/resenas.jsp").forward(request, response);
    }
    
    /**
     * Listar reseñas de un producto específico
     */
    private void listarPorProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int productoId = Integer.parseInt(request.getParameter("productoId"));
        List<Resena> resenas = resenaBO.obtenerPorProducto(productoId);
        
        request.setAttribute("resenas", resenas);
        request.setAttribute("productoId", productoId);
        request.getRequestDispatcher("/admin/resenas.jsp").forward(request, response);
    }
    
    /**
     * Eliminar reseña (moderación)
     */
    private void eliminarResena(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        int id = Integer.parseInt(request.getParameter("id"));
        resenaBO.eliminar(id);
        
        // Redireccionar con mensaje de éxito
        response.sendRedirect(request.getContextPath() + "/admin/resenas?success=deleted");
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet de Moderación de Reseñas";
    }
}
