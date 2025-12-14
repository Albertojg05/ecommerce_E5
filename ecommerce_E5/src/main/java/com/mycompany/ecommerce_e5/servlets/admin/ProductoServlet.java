/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.admin;

import com.mycompany.ecommerce_e5.bo.CategoriaBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.bo.ProductoTallaBO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.dominio.ProductoTalla;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para la gestion de productos desde el panel de administracion.
 * Permite listar, crear, editar y eliminar productos del catalogo.
 * Usa el parametro "accion" para determinar que operacion realizar.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "AdminProductoServlet", urlPatterns = {"/admin/producto"})
public class ProductoServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;
    private ProductoTallaBO productoTallaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
        productoTallaBO = new ProductoTallaBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        try {
            if (accion == null || accion.equals("listar")) {
                listarProductos(request, response);
            } else if (accion.equals("nuevo")) {
                mostrarFormularioNuevo(request, response);
            } else if (accion.equals("editar")) {
                mostrarFormularioEditar(request, response);
            } else if (accion.equals("eliminar")) {
                eliminarProducto(request, response);
            }
        } catch (Exception e) {
            String mensajeError = e.getMessage();
            // Mensaje amigable para error de foreign key
            if (mensajeError != null && mensajeError.contains("transaction")) {
                mensajeError = "No se puede eliminar el producto porque tiene pedidos, reseñas o items en carritos asociados";
            }
            request.setAttribute("error", "Error: " + mensajeError);
            // Cargar productos para mostrar la lista
            List<Producto> productos = productoBO.obtenerTodosConTallas();
            request.setAttribute("productos", productos);
            request.getRequestDispatcher("/admin/producto.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        try {
            if (accion.equals("crear")) {
                crearProducto(request, response);
            } else if (accion.equals("actualizar")) {
                actualizarProducto(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());

            // Si es actualizar, volver al formulario de edición
            if (accion.equals("actualizar")) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    try {
                        int id = Integer.parseInt(idStr);
                        Producto producto = productoBO.obtenerPorId(id);
                        List<Categoria> categorias = categoriaBO.obtenerTodas();
                        request.setAttribute("producto", producto);
                        request.setAttribute("categorias", categorias);
                        request.getRequestDispatcher("/admin/producto-form.jsp").forward(request, response);
                        return;
                    } catch (Exception ex) {
                        // Si falla, continuar al listado
                    }
                }
            }

            // Para crear o si falla el redirect de actualizar, mostrar listado
            List<Producto> productos = productoBO.obtenerTodosConTallas();
            request.setAttribute("productos", productos);
            request.getRequestDispatcher("/admin/producto.jsp").forward(request, response);
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Producto> productos = productoBO.obtenerTodosConTallas();
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/admin/producto.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Categoria> categorias = categoriaBO.obtenerTodas();
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("/admin/producto-form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {

        int id = Integer.parseInt(request.getParameter("id"));
        Producto producto = productoBO.obtenerPorId(id);
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        List<ProductoTalla> tallas = productoTallaBO.obtenerTallasPorProducto(id);

        request.setAttribute("producto", producto);
        request.setAttribute("categorias", categorias);
        request.setAttribute("tallasProducto", tallas);
        request.getRequestDispatcher("/admin/producto-form.jsp").forward(request, response);
    }

    private void crearProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String precioStr = request.getParameter("precio");
        String imagenUrl = request.getParameter("imagenUrl");
        String color = request.getParameter("color");
        String categoriaIdStr = request.getParameter("categoriaId");

        // Obtener tallas y stocks del formulario
        String[] tallasArr = request.getParameterValues("tallas[]");
        String[] stocksArr = request.getParameterValues("stocks[]");

        // Validar precio positivo
        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                throw new Exception("El precio debe ser mayor a 0");
            }
        } catch (NumberFormatException e) {
            throw new Exception("El precio debe ser un número válido");
        }

        // Validar URL de imagen
        if (imagenUrl != null && !imagenUrl.trim().isEmpty()) {
            imagenUrl = imagenUrl.trim();
            if (!imagenUrl.matches("^(https?://.*|imgs/.*|/.*\\.(jpg|jpeg|png|gif|webp))$") &&
                !imagenUrl.startsWith("imgs/")) {
                throw new Exception("La URL de imagen no es válida");
            }
        }

        // Validar categoría
        int categoriaId;
        try {
            categoriaId = Integer.parseInt(categoriaIdStr);
        } catch (NumberFormatException e) {
            throw new Exception("Debe seleccionar una categoría válida");
        }

        Categoria categoria = categoriaBO.obtenerPorId(categoriaId);
        if (categoria == null) {
            throw new Exception("La categoría seleccionada no existe");
        }

        // Validar color
        if (color != null && !color.trim().isEmpty()) {
            if (!color.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ,\\s/]+$")) {
                throw new Exception("El color solo puede contener letras, comas, espacios y /");
            }
        }

        // Validar que haya al menos una talla
        if (tallasArr == null || tallasArr.length == 0) {
            throw new Exception("Debe agregar al menos una talla");
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setImagenUrl(imagenUrl);
        producto.setColor(color != null ? color.trim() : null);
        producto.setCategoria(categoria);

        Producto productoGuardado = productoBO.crear(producto);

        // Guardar las tallas
        for (int i = 0; i < tallasArr.length; i++) {
            String tallaNombre = tallasArr[i];
            if (tallaNombre != null && !tallaNombre.trim().isEmpty()) {
                int stock = 0;
                if (stocksArr != null && i < stocksArr.length) {
                    try {
                        stock = Integer.parseInt(stocksArr[i]);
                        if (stock < 0) stock = 0;
                    } catch (NumberFormatException e) {
                        stock = 0;
                    }
                }
                productoTallaBO.guardarTalla(productoGuardado, tallaNombre.trim(), stock);
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/producto?success=created");
    }

    private void actualizarProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String precioStr = request.getParameter("precio");
        String imagenUrl = request.getParameter("imagenUrl");
        String color = request.getParameter("color");
        String categoriaIdStr = request.getParameter("categoriaId");

        // Obtener tallas y stocks del formulario
        String[] tallasArr = request.getParameterValues("tallas[]");
        String[] stocksArr = request.getParameterValues("stocks[]");
        String[] tallaIdsArr = request.getParameterValues("tallaIds[]");

        // Validar precio positivo
        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                throw new Exception("El precio debe ser mayor a 0");
            }
        } catch (NumberFormatException e) {
            throw new Exception("El precio debe ser un número válido");
        }

        // Validar URL de imagen
        if (imagenUrl != null && !imagenUrl.trim().isEmpty()) {
            imagenUrl = imagenUrl.trim();
            if (!imagenUrl.matches("^(https?://.*|imgs/.*|/.*\\.(jpg|jpeg|png|gif|webp))$") &&
                !imagenUrl.startsWith("imgs/")) {
                throw new Exception("La URL de imagen no es válida");
            }
        }

        // Validar categoría
        int categoriaId;
        try {
            categoriaId = Integer.parseInt(categoriaIdStr);
        } catch (NumberFormatException e) {
            throw new Exception("Debe seleccionar una categoría válida");
        }

        Categoria categoria = categoriaBO.obtenerPorId(categoriaId);
        if (categoria == null) {
            throw new Exception("La categoría seleccionada no existe");
        }

        // Validar color
        if (color != null && !color.trim().isEmpty()) {
            if (!color.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ,\\s/]+$")) {
                throw new Exception("El color solo puede contener letras, comas, espacios y /");
            }
        }

        // Validar que haya al menos una talla
        if (tallasArr == null || tallasArr.length == 0) {
            throw new Exception("Debe agregar al menos una talla");
        }

        Producto producto = productoBO.obtenerPorId(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setImagenUrl(imagenUrl);
        producto.setColor(color != null ? color.trim() : null);
        producto.setCategoria(categoria);

        productoBO.actualizar(producto);

        // Actualizar las tallas
        for (int i = 0; i < tallasArr.length; i++) {
            String tallaNombre = tallasArr[i];
            if (tallaNombre != null && !tallaNombre.trim().isEmpty()) {
                int stock = 0;
                if (stocksArr != null && i < stocksArr.length) {
                    try {
                        stock = Integer.parseInt(stocksArr[i]);
                        if (stock < 0) stock = 0;
                    } catch (NumberFormatException e) {
                        stock = 0;
                    }
                }

                // Si tiene ID, actualizar; si no, crear nueva
                if (tallaIdsArr != null && i < tallaIdsArr.length && !tallaIdsArr[i].isEmpty()) {
                    try {
                        int tallaId = Integer.parseInt(tallaIdsArr[i]);
                        productoTallaBO.actualizarStock(tallaId, stock);
                    } catch (NumberFormatException e) {
                        productoTallaBO.guardarOActualizarTalla(producto, tallaNombre.trim(), stock);
                    }
                } else {
                    productoTallaBO.guardarOActualizarTalla(producto, tallaNombre.trim(), stock);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/producto?success=updated");
    }

    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int id = Integer.parseInt(request.getParameter("id"));
        productoBO.eliminar(id);

        response.sendRedirect(request.getContextPath() + "/admin/producto?success=deleted");
    }

    @Override
    public String getServletInfo() {
        return "Servlet de Gestión de Productos";
    }
}
