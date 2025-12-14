package com.mycompany.ecommerce_e5.rest;

import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.dominio.DetallePedido;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.rest.dto.ApiResponse;
import com.mycompany.ecommerce_e5.rest.dto.CarritoItemDTO;
import com.mycompany.ecommerce_e5.util.ConfiguracionTienda;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST para el carrito de compras.
 * IMPORTANTE: Usa la misma estructura List<DetallePedido> que el servlet original
 * para mantener compatibilidad.
 *
 * Endpoints: GET /api/carrito, POST /api/carrito, PUT /api/carrito/{id}, DELETE /api/carrito/{id}
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Path("/carrito")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarritoResource {

    private final ProductoBO productoBO = new ProductoBO();

    @Context
    private HttpServletRequest request;

    /**
     * GET /api/carrito
     * Obtiene el contenido del carrito.
     */
    @GET
    public Response getCarrito() {
        HttpSession session = request.getSession(true);
        List<DetallePedido> carrito = obtenerCarritoFromSession(session);

        // Convertir a DTOs para respuesta JSON
        List<CarritoItemDTO> items = new ArrayList<>();
        double subtotal = 0;

        for (DetallePedido detalle : carrito) {
            CarritoItemDTO item = new CarritoItemDTO(
                    detalle.getProducto().getId(),
                    detalle.getProducto().getNombre(),
                    detalle.getPrecioUnitario(),
                    detalle.getCantidad(),
                    detalle.getProducto().getImagenUrl()
            );
            items.add(item);
            subtotal += detalle.getPrecioUnitario() * detalle.getCantidad();
        }

        double envio = ConfiguracionTienda.calcularCostoEnvio(subtotal);
        double total = subtotal + envio;

        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("cantidadItems", carrito.size());
        data.put("subtotal", subtotal);
        data.put("envio", envio);
        data.put("total", total);
        data.put("envioGratisDesde", ConfiguracionTienda.MONTO_MINIMO_ENVIO_GRATIS);

        return Response.ok(ApiResponse.ok(data)).build();
    }

    /**
     * POST /api/carrito
     * Agrega un producto al carrito.
     * Body: { "productoId": 1, "cantidad": 2 }
     */
    @POST
    public Response agregarAlCarrito(Map<String, Integer> body) {
        try {
            Integer productoId = body.get("productoId");
            Integer cantidad = body.get("cantidad");

            if (productoId == null || cantidad == null || cantidad <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Producto y cantidad son requeridos"))
                        .build();
            }

            // Validar cantidad dentro de límites
            if (!ConfiguracionTienda.esCantidadValida(cantidad)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Máximo " +
                                ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO + " unidades por producto"))
                        .build();
            }

            Producto producto = productoBO.obtenerPorId(productoId);
            if (producto == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Producto no encontrado"))
                        .build();
            }

            HttpSession session = request.getSession(true);
            List<DetallePedido> carrito = obtenerCarritoFromSession(session);

            // Calcular cantidad total (existente + nueva)
            int cantidadExistente = obtenerCantidadEnCarrito(carrito, productoId);
            int cantidadTotal = cantidadExistente + cantidad;

            // Validar stock
            if (producto.getExistencias() < cantidadTotal) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Stock insuficiente. Disponible: " + producto.getExistencias()))
                        .build();
            }

            // Validar cantidad máxima por producto
            if (cantidadTotal > ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("No puedes agregar más de " +
                                ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO + " unidades del mismo producto"))
                        .build();
            }

            // Agregar o actualizar en carrito
            boolean existe = false;
            for (DetallePedido detalle : carrito) {
                if (detalle.getProducto().getId() == productoId) {
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

            // Respuesta con el item agregado
            CarritoItemDTO itemDTO = new CarritoItemDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidadTotal,
                    producto.getImagenUrl()
            );

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.ok("Producto agregado al carrito", itemDTO))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al agregar al carrito: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * PUT /api/carrito/{productoId}
     * Actualiza la cantidad de un producto en el carrito.
     * Body: { "cantidad": 3 }
     */
    @PUT
    @Path("/{productoId}")
    public Response actualizarCantidad(@PathParam("productoId") int productoId, Map<String, Integer> body) {
        try {
            Integer cantidad = body.get("cantidad");

            if (cantidad == null || cantidad < ConfiguracionTienda.CANTIDAD_MINIMA_PRODUCTO) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("La cantidad mínima es " + ConfiguracionTienda.CANTIDAD_MINIMA_PRODUCTO))
                        .build();
            }

            if (cantidad > ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("La cantidad máxima por producto es " + ConfiguracionTienda.CANTIDAD_MAXIMA_PRODUCTO))
                        .build();
            }

            Producto producto = productoBO.obtenerPorId(productoId);
            if (producto.getExistencias() < cantidad) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Stock insuficiente. Disponible: " + producto.getExistencias()))
                        .build();
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Carrito vacío"))
                        .build();
            }

            List<DetallePedido> carrito = obtenerCarritoFromSession(session);
            boolean encontrado = false;

            for (DetallePedido detalle : carrito) {
                if (detalle.getProducto().getId() == productoId) {
                    detalle.setCantidad(cantidad);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Producto no está en el carrito"))
                        .build();
            }

            session.setAttribute("carrito", carrito);

            CarritoItemDTO itemDTO = new CarritoItemDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidad,
                    producto.getImagenUrl()
            );

            return Response.ok(ApiResponse.ok("Cantidad actualizada", itemDTO)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al actualizar: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * DELETE /api/carrito/{productoId}
     * Elimina un producto del carrito.
     */
    @DELETE
    @Path("/{productoId}")
    public Response eliminarDelCarrito(@PathParam("productoId") int productoId) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Carrito vacío"))
                    .build();
        }

        List<DetallePedido> carrito = obtenerCarritoFromSession(session);
        boolean removed = carrito.removeIf(detalle -> detalle.getProducto().getId() == productoId);

        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Producto no está en el carrito"))
                    .build();
        }

        session.setAttribute("carrito", carrito);

        return Response.ok(ApiResponse.ok("Producto eliminado del carrito", null)).build();
    }

    /**
     * DELETE /api/carrito
     * Vacía el carrito.
     */
    @DELETE
    public Response vaciarCarrito() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("carrito");
        }
        return Response.ok(ApiResponse.ok("Carrito vaciado", null)).build();
    }

    /**
     * Obtiene el carrito de la sesión (compatible con servlet original).
     */
    @SuppressWarnings("unchecked")
    private List<DetallePedido> obtenerCarritoFromSession(HttpSession session) {
        Object carritoObj = session.getAttribute("carrito");
        if (carritoObj instanceof List) {
            return (List<DetallePedido>) carritoObj;
        }
        return new ArrayList<>();
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
}
