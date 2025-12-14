/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.admin;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.bo.ResenaBO;
import com.mycompany.ecommerce_e5.dominio.Pedido;
import com.mycompany.ecommerce_e5.dominio.Resena;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet del panel de control (dashboard) del administrador.
 * Muestra estadisticas generales de la tienda: total de productos,
 * pedidos, resenas, productos con stock bajo y los pedidos mas recientes.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    private ProductoBO productoBO;
    private PedidoBO pedidoBO;
    private ResenaBO resenaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        pedidoBO = new PedidoBO();
        resenaBO = new ResenaBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener conteos para el dashboard (evita cargar todos los registros)
            long totalProductos = productoBO.contarProductos();
            List<Pedido> pedidos = pedidoBO.obtenerTodos();
            List<Resena> resenas = resenaBO.obtenerTodas();

            // Calcular estadísticas
            int totalPedidos = pedidos.size();
            int totalResenas = resenas.size();

            // Productos con bajo stock (menos de 10 unidades en total)
            // Usa consulta directa para evitar LazyInitializationException
            long productosStockBajo = productoBO.contarProductosStockBajo(10);

            // Pasar datos a la vista
            request.setAttribute("totalProductos", totalProductos);
            request.setAttribute("totalPedidos", totalPedidos);
            request.setAttribute("totalResenas", totalResenas);
            request.setAttribute("productosStockBajo", productosStockBajo);

            // Pedidos recientes (últimos 5)
            List<Pedido> pedidosRecientes = pedidos.subList(0, Math.min(5, pedidos.size()));
            request.setAttribute("pedidosRecientes", pedidosRecientes);

            // Forward a la vista JSP
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet del Dashboard de Administrador";
    }
}
