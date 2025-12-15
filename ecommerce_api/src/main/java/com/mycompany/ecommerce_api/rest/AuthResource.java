package com.mycompany.ecommerce_api.rest;

import com.mycompany.ecommerce_api.bo.UsuarioBO;
import com.mycompany.ecommerce_api.dominio.Usuario;
import com.mycompany.ecommerce_api.dominio.enums.RolUsuario;
import com.mycompany.ecommerce_api.excepciones.BusinessException;
import com.mycompany.ecommerce_api.rest.dto.ApiResponse;
import com.mycompany.ecommerce_api.rest.dto.AuthResponse;
import com.mycompany.ecommerce_api.rest.dto.LoginRequest;
import com.mycompany.ecommerce_api.rest.dto.RegistroRequest;
import com.mycompany.ecommerce_api.rest.dto.UsuarioDTO;
import com.mycompany.ecommerce_api.util.JwtUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * API REST para autenticacion de usuarios.
 * Endpoints:
 * - POST /api/auth/login - Iniciar sesion
 * - POST /api/auth/registro - Registrar nuevo usuario
 * - GET /api/auth/perfil - Obtener perfil del usuario autenticado
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

    /**
     * POST /api/auth/login
     * Autentica un usuario con correo y contrasena.
     *
     * @param loginRequest Datos de login (correo, contrasena)
     * @return Token JWT y datos del usuario si las credenciales son validas
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

            String token = JwtUtil.generarToken((long) usuario.getId(), usuario.getCorreo(), usuario.getRol());
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);

            return Response.ok(new AuthResponse(token, usuarioDTO)).build();

        } catch (BusinessException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error interno del servidor"))
                    .build();
        }
    }

    /**
     * POST /api/auth/registro
     * Registra un nuevo usuario con rol CLIENTE.
     *
     * @param request Datos de registro (nombre, correo, contrasena)
     * @return Token JWT y datos del usuario registrado
     */
    @POST
    @Path("/registro")
    public Response registro(RegistroRequest request) {
        try {
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Datos de registro requeridos"))
                        .build();
            }

            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("El nombre es requerido"))
                        .build();
            }

            if (request.getCorreo() == null || request.getCorreo().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("El correo es requerido"))
                        .build();
            }

            if (request.getContrasena() == null || request.getContrasena().length() < 6) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("La contraseña debe tener al menos 6 caracteres"))
                        .build();
            }

            // Crear usuario con rol CLIENTE
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(request.getNombre().trim());
            nuevoUsuario.setCorreo(request.getCorreo().trim());
            nuevoUsuario.setContrasena(request.getContrasena());
            nuevoUsuario.setTelefono(request.getTelefono());
            nuevoUsuario.setRol(RolUsuario.CLIENTE);

            Usuario usuarioRegistrado = usuarioBO.registrar(nuevoUsuario);

            String token = JwtUtil.generarToken(
                    (long) usuarioRegistrado.getId(),
                    usuarioRegistrado.getCorreo(),
                    usuarioRegistrado.getRol()
            );

            UsuarioDTO usuarioDTO = new UsuarioDTO(usuarioRegistrado);

            return Response.status(Response.Status.CREATED)
                    .entity(new AuthResponse(token, usuarioDTO))
                    .build();

        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error interno del servidor"))
                    .build();
        }
    }

    /**
     * GET /api/auth/perfil
     * Obtiene el perfil del usuario autenticado.
     * Requiere token JWT valido.
     *
     * @param requestContext Contexto de la solicitud con userId del token
     * @return Datos del usuario autenticado
     */
    @GET
    @Path("/perfil")
    public Response perfil(@Context ContainerRequestContext requestContext) {
        try {
            Long userId = (Long) requestContext.getProperty("userId");

            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Token no válido"))
                        .build();
            }

            Usuario usuario = usuarioBO.obtenerPorId(userId.intValue());

            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Usuario no encontrado"))
                        .build();
            }

            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);

            return Response.ok(ApiResponse.ok(usuarioDTO)).build();

        } catch (BusinessException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error interno del servidor"))
                    .build();
        }
    }
}
