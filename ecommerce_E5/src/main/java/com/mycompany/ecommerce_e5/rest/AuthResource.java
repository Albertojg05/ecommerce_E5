package com.mycompany.ecommerce_e5.rest;

import com.mycompany.ecommerce_e5.bo.UsuarioBO;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.rest.dto.ApiResponse;
import com.mycompany.ecommerce_e5.rest.dto.LoginRequest;
import com.mycompany.ecommerce_e5.rest.dto.UsuarioDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * API REST para autenticacion de clientes.
 * Endpoints: POST /api/auth/login, POST /api/auth/logout, GET /api/auth/status
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UsuarioBO usuarioBO = new UsuarioBO();

    @Context
    private HttpServletRequest request;

    /**
     * POST /api/auth/login
     * Inicia sesion del cliente.
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getCorreo() == null || loginRequest.getContrasena() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Correo y contraseña son requeridos"))
                        .build();
            }

            Usuario usuario = usuarioBO.login(loginRequest.getCorreo(), loginRequest.getContrasena());

            if (usuario == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Credenciales incorrectas"))
                        .build();
            }

            // Crear sesion
            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("esAdmin", usuario.getRol().name().equals("ADMINISTRADOR"));

            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);
            return Response.ok(ApiResponse.ok("Inicio de sesión exitoso", usuarioDTO)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/auth/logout
     * Cierra sesion del cliente.
     */
    @POST
    @Path("/logout")
    public Response logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok(ApiResponse.ok("Sesión cerrada exitosamente", null)).build();
    }

    /**
     * GET /api/auth/status
     * Verifica si el usuario esta autenticado.
     */
    @GET
    @Path("/status")
    public Response getStatus() {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);
            return Response.ok(ApiResponse.ok("Autenticado", usuarioDTO)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.error("No autenticado"))
                .build();
    }
}
