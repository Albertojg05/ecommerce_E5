package com.mycompany.ecommerce_api.rest;

import com.mycompany.ecommerce_api.bo.UsuarioBO;
import com.mycompany.ecommerce_api.dominio.Usuario;
import com.mycompany.ecommerce_api.rest.dto.ApiResponse;
import com.mycompany.ecommerce_api.rest.dto.LoginRequest;
import com.mycompany.ecommerce_api.rest.dto.UsuarioDTO;
import com.mycompany.ecommerce_api.util.JwtUtil;
import jakarta.ws.rs.*;
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

    /**
     * POST /api/auth/login
     * Inicia sesion del cliente.
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            Usuario usuario = usuarioBO.login(loginRequest.getCorreo(), loginRequest.getContrasena());

            if (usuario == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Credenciales incorrectas"))
                        .build();
            }

            String token = JwtUtil.generateToken(usuario);
            
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario); 
            
            return Response.ok(new AuthResponse(token, usuarioDTO)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }
    
    public static class AuthResponse {
        public String token;
        public UsuarioDTO usuario;
        public AuthResponse(String token, UsuarioDTO usuario) {
            this.token = token;
            this.usuario = usuario;
        }
    }
}
