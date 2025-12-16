/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.cliente;

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
 * Servlet de la pagina principal (home) de la tienda.
 * Muestra las categorias disponibles y una seleccion de productos destacados
 * para que el cliente pueda ver las novedades.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "ClienteHomeServlet", urlPatterns = {"/index", ""})
public class ClienteHomeServlet extends HttpServlet {

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
            List<Producto> productos = productoBO.obtenerTodos();
            List<Producto> destacados = productos.size() > 12 ? productos.subList(0, 12) : productos;
            request.setAttribute("productosDestacados", destacados);
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
