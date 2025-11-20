/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets;

import com.mycompany.ecommerce_e5.bo.CategoriaBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.dominio.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595 
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/index", ""})
public class IndexServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            List<Categoria> categorias = categoriaBO.obtenerTodas();
            request.setAttribute("categorias", categorias);

            List<Producto> productosDestacados = productoBO.obtenerTodos();
            if (productosDestacados.size() > 10) {
                productosDestacados = productosDestacados.subList(0, 10);
            }
            request.setAttribute("productosDestacados", productosDestacados);

            // Página principal
            request.getRequestDispatcher("/index.html").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el catálogo: " + e.getMessage());
            request.getRequestDispatcher("/index.html").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet de la página principal";
    }
}
