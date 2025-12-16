package com.mycompany.ecommerce_e5.rest;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.dao.DireccionDAO;
import com.mycompany.ecommerce_e5.dominio.*;
import com.mycompany.ecommerce_e5.dominio.enums.MetodoPago;
import com.mycompany.ecommerce_e5.rest.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

/**
 * API REST para pedidos.
 * IMPORTANTE: Usa PedidoBO.crearPedido() igual que el servlet original
 * para mantener compatibilidad y reutilizar la lógica de negocio.
 *
 * Endpoints: GET /api/pedidos, GET /api/pedidos/{id}, POST /api/pedidos (checkout)
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {

    private final PedidoBO pedidoBO = new PedidoBO();
    private final DireccionDAO direccionDAO = new DireccionDAO();

    @Context
    private HttpServletRequest request;

    /**
     * GET /api/pedidos
     * Lista los pedidos del usuario autenticado.
     */
    @GET
    public Response getPedidos() {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            List<Pedido> pedidos = pedidoBO.obtenerPorUsuario(usuario.getId());

            List<PedidoDTO> pedidosDTO = new ArrayList<>();
            for (Pedido p : pedidos) {
                pedidosDTO.add(new PedidoDTO(p));
            }

            return Response.ok(ApiResponse.ok(pedidosDTO)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener pedidos: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/pedidos/{id}
     * Obtiene el detalle de un pedido.
     */
    @GET
    @Path("/{id}")
    public Response getPedido(@PathParam("id") int id) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            Pedido pedido = pedidoBO.obtenerPorId(id);

            if (pedido == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Pedido no encontrado"))
                        .build();
            }

            // Verificar que el pedido pertenece al usuario
            if (pedido.getUsuario().getId() != usuario.getId()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(ApiResponse.error("No tiene permiso para ver este pedido"))
                        .build();
            }

            return Response.ok(ApiResponse.ok(new PedidoDTO(pedido))).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener pedido: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/pedidos
     * Crea un nuevo pedido (checkout).
     * Body: { "direccionId": 1, "metodoPago": "TARJETA" }
     * o para nueva dirección:
     * { "calle": "...", "ciudad": "...", "estado": "...", "codigoPostal": "...", "metodoPago": "TARJETA" }
     */
    @POST
    public Response crearPedido(CheckoutRequest checkoutRequest) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión para realizar un pedido"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

            // Obtener carrito (misma estructura que el servlet original)
            @SuppressWarnings("unchecked")
            List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");

            if (carrito == null || carrito.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("El carrito está vacío"))
                        .build();
            }

            // Obtener o crear dirección
            Direccion direccion;
            if (checkoutRequest.getDireccionId() > 0) {
                // Usar dirección existente
                direccion = direccionDAO.obtenerPorId(checkoutRequest.getDireccionId());
                if (direccion == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponse.error("Dirección no válida"))
                            .build();
                }
            } else {
                // Crear nueva dirección (si se proporcionan los datos)
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Debe seleccionar una dirección de envío"))
                        .build();
            }

            // Validar método de pago
            MetodoPago metodoPago;
            try {
                metodoPago = MetodoPago.valueOf(checkoutRequest.getMetodoPago());
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Método de pago no válido. Use TARJETA o PAYPAL"))
                        .build();
            }

            // Usar el método crearPedido del BO (igual que el servlet)
            Pedido pedido = pedidoBO.crearPedido(usuario, direccion, carrito, metodoPago);

            // Limpiar carrito
            session.removeAttribute("carrito");

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.ok("Pedido creado exitosamente", new PedidoDTO(pedido)))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Error al crear pedido: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/pedidos/{id}/cancelar
     * Cancela un pedido pendiente.
     */
    @POST
    @Path("/{id}/cancelar")
    public Response cancelarPedido(@PathParam("id") int id) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            Pedido pedido = pedidoBO.obtenerPorId(id);

            if (pedido == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Pedido no encontrado"))
                        .build();
            }

            // Verificar que el pedido pertenece al usuario
            if (pedido.getUsuario().getId() != usuario.getId()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(ApiResponse.error("No tiene permiso para cancelar este pedido"))
                        .build();
            }

            pedidoBO.cancelarPedido(id);

            return Response.ok(ApiResponse.ok("Pedido cancelado exitosamente", null)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }
}
