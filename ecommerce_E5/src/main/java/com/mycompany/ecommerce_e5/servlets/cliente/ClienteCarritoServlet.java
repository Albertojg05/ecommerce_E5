/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.cliente;

import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.dominio.DetallePedido;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.excepciones.BusinessException;
import com.mycompany.ecommerce_e5.excepciones.StockInsuficienteException;
import com.mycompany.ecommerce_e5.util.ConfiguracionTienda;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet del carrito de compras del cliente.
 * Maneja las operaciones del carrito: agregar productos, eliminar,
 * actualizar cantidades y mostrar el resumen con subtotales.
 * El carrito se guarda en la sesion del usuario.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "ClienteCarritoServlet", urlPatterns = {"/cliente/carrito"})
public class ClienteCarritoServlet extends HttpServlet {

    private ProductoBO productoBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<DetallePedido> carrito = obtenerCarrito(session);

        // Validar y limpiar productos eliminados o con stock insuficiente (#19 y #20)
        List<String> advertencias = validarYLimpiarCarrito(carrito, session);
        if (!advertencias.isEmpty()) {
            request.setAttribute("advertencias", advertencias);
        }

        double subtotal = calcularSubtotal(carrito);
        double costoEnvio = ConfiguracionTienda.calcularCostoEnvio(subtotal);
        double faltaEnvioGratis = ConfiguracionTienda.faltaParaEnvioGratis(subtotal);

        request.setAttribute("subtotal", subtotal);
        request.setAttribute("costoEnvio", costoEnvio);
        request.setAttribute("total", subtotal + costoEnvio);
        request.setAttribute("faltaEnvioGratis", faltaEnvioGratis);
        request.setAttribute("montoMinimoEnvioGratis", ConfiguracionTienda.MONTO_MINIMO_ENVIO_GRATIS);

        request.getRequestDispatcher("/cliente/carrito.jsp").forward(request, response);
    }

    /**
     * Valida y limpia el carrito eliminando productos inexistentes
     * y ajustando cantidades según stock disponible.
     * @return Lista de advertencias para mostrar al usuario
     */
    private List<String> validarYLimpiarCarrito(List<DetallePedido> carrito, HttpSession session) {
        List<String> advertencias = new ArrayList<>();
        List<DetallePedido> itemsAEliminar = new ArrayList<>();

        for (DetallePedido detalle : carrito) {
            try {
                // Verificar si el producto aún existe
                Producto productoActual = productoBO.obtenerPorId(detalle.getProducto().getId());

                if (productoActual == null) {
                    // Producto fue eliminado de la BD (#19)
                    advertencias.add("\"" + detalle.getProducto().getNombre() + "\" ya no está disponible y fue eliminado del carrito");
                    itemsAEliminar.add(detalle);
                } else {
                    // Actualizar precio por si cambió
                    detalle.setPrecioUnitario(productoActual.getPrecio());
                    detalle.setProducto(productoActual);

                    // Verificar stock disponible (#20)
                    if (productoActual.getExistencias() <= 0) {
                        advertencias.add("\"" + productoActual.getNombre() + "\" está agotado y fue eliminado del carrito");
                        itemsAEliminar.add(detalle);
                    } else if (detalle.getCantidad() > productoActual.getExistencias()) {
                        // Ajustar cantidad al stock disponible
                        int cantidadAnterior = detalle.getCantidad();
                        detalle.setCantidad(productoActual.getExistencias());
                        advertencias.add("\"" + productoActual.getNombre() + "\": cantidad ajustada de "
                                + cantidadAnterior + " a " + productoActual.getExistencias() + " (stock disponible)");
                    }
                }
            } catch (Exception e) {
                // Error al obtener producto, asumimos que no existe
                advertencias.add("\"" + detalle.getProducto().getNombre() + "\" ya no está disponible");
                itemsAEliminar.add(detalle);
            }
        }

        // Eliminar items marcados
        carrito.removeAll(itemsAEliminar);
        session.setAttribute("carrito", carrito);

        return advertencias;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        try {
            switch (accion) {
                case "agregar":
                    agregar(request, session);
                    break;
                case "actualizar":
                    actualizar(request, session);
                    break;
                case "eliminar":
                    eliminar(request, session);
                    break;
                case "vaciar":
                    session.removeAttribute("carrito");
                    break;
                default:
                    break;
            }
            response.sendRedirect(request.getContextPath() + "/cliente/carrito");

        } catch (StockInsuficienteException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("stockDisponible", e.getStockDisponible());
            request.setAttribute("productoId", e.getProductoId());
            doGet(request, response);

        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void agregar(HttpServletRequest req, HttpSession session) throws BusinessException {
        int id = Integer.parseInt(req.getParameter("productoId"));
        int cantidad = Integer.parseInt(req.getParameter("cantidad"));

        // Validar cantidad dentro de límites
        if (!ConfiguracionTienda.esCantidadValida(cantidad)) {
            throw new BusinessException(String.format(
                    "La cantidad debe estar entre %d y %d",
                    ConfiguracionTienda.CANTIDAD_MINIMA_PRODUCTO,
                    ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO));
        }

        Producto producto = productoBO.obtenerPorId(id);
        List<DetallePedido> carrito = obtenerCarrito(session);

        // Calcular cantidad total (existente + nueva)
        int cantidadExistente = obtenerCantidadEnCarrito(carrito, id);
        int cantidadTotal = cantidadExistente + cantidad;

        // Validar stock para cantidad total
        productoBO.validarStock(id, cantidadTotal);

        // Validar cantidad máxima por producto
        if (cantidadTotal > ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO) {
            throw new BusinessException(String.format(
                    "No puedes agregar más de %d unidades del mismo producto",
                    ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO));
        }

        // Agregar o actualizar en carrito
        boolean existe = false;
        for (DetallePedido detalle : carrito) {
            if (detalle.getProducto().getId() == id) {
                detalle.setCantidad(cantidadTotal);
                existe = true;
                break;
            }
        }

        if (!existe) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            carrito.add(detalle);
        }

        session.setAttribute("carrito", carrito);
    }

    private void actualizar(HttpServletRequest req, HttpSession session) throws BusinessException {
        int id = Integer.parseInt(req.getParameter("productoId"));
        int cantidad = Integer.parseInt(req.getParameter("cantidad"));

        // Validar cantidad mínima
        if (cantidad < ConfiguracionTienda.CANTIDAD_MINIMA_PRODUCTO) {
            throw new BusinessException("La cantidad mínima es "
                    + ConfiguracionTienda.CANTIDAD_MINIMA_PRODUCTO);
        }

        // Validar cantidad máxima
        if (cantidad > ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO) {
            throw new BusinessException(String.format(
                    "La cantidad máxima por producto es %d",
                    ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO));
        }

        // Validar stock disponible
        productoBO.validarStock(id, cantidad);

        // Actualizar cantidad en carrito
        List<DetallePedido> carrito = obtenerCarrito(session);
        for (DetallePedido detalle : carrito) {
            if (detalle.getProducto().getId() == id) {
                detalle.setCantidad(cantidad);
                break;
            }
        }

        session.setAttribute("carrito", carrito);
    }

    private void eliminar(HttpServletRequest req, HttpSession session) {
        int id = Integer.parseInt(req.getParameter("productoId"));
        List<DetallePedido> carrito = obtenerCarrito(session);
        carrito.removeIf(detalle -> detalle.getProducto().getId() == id);
        session.setAttribute("carrito", carrito);
    }

    /**
     * Obtiene el carrito de la sesión o crea uno nuevo.
     */
    @SuppressWarnings("unchecked")
    private List<DetallePedido> obtenerCarrito(HttpSession session) {
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    /**
     * Calcula el subtotal del carrito.
     */
    private double calcularSubtotal(List<DetallePedido> carrito) {
        double subtotal = 0;
        for (DetallePedido detalle : carrito) {
            subtotal += detalle.getPrecioUnitario() * detalle.getCantidad();
        }
        return subtotal;
    }

    /**
     * Obtiene la cantidad actual de un producto en el carrito.
     */
    private int obtenerCantidadEnCarrito(List<DetallePedido> carrito, int productoId) {
        for (DetallePedido detalle : carrito) {
            if (detalle.getProducto().getId() == productoId) {
                return detalle.getCantidad();
            }
        }
        return 0;
    }

    @Override
    public String getServletInfo() {
        return "Carrito de compras del cliente";
    }
}
