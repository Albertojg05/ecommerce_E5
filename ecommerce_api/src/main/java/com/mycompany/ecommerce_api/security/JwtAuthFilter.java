package com.mycompany.ecommerce_api.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mycompany.ecommerce_api.dominio.enums.RolUsuario;
import com.mycompany.ecommerce_api.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filtro global JAX-RS para autenticación JWT basado en rutas.
 * Intercepta todas las peticiones a /api/* y aplica reglas de seguridad.
 *
 * Rutas públicas: auth/login, auth/registro, productos (GET), categorias (GET)
 * Rutas protegidas: carrito/*, pedidos/*, perfil/*, resenas (POST)
 * Rutas admin: admin/* (requieren rol ADMINISTRADOR)
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();
        String method = ctx.getMethod();

        // Permitir preflight CORS
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        // Verificar si es ruta pública
        if (isPublicRoute(path, method)) {
            return;
        }

        // Extraer y validar token
        String authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abortUnauthorized(ctx, "Token no proporcionado");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            DecodedJWT jwt = JwtUtil.validarToken(token);

            Long userId = jwt.getClaim("userId").asLong();
            String email = jwt.getSubject();
            String rolStr = jwt.getClaim("rol").asString();

            // Guardar datos en contexto para uso en recursos
            ctx.setProperty("userId", userId);
            ctx.setProperty("email", email);
            ctx.setProperty("rol", rolStr);

            // Verificar acceso a rutas admin
            if (isAdminRoute(path)) {
                if (!RolUsuario.ADMINISTRADOR.name().equals(rolStr)) {
                    abortForbidden(ctx);
                    return;
                }
            }

        } catch (JWTVerificationException e) {
            abortUnauthorized(ctx, "Token inválido o expirado");
        }
    }

    /**
     * Determina si una ruta es pública (no requiere autenticación).
     */
    private boolean isPublicRoute(String path, String method) {
        // Normalizar path
        path = path.startsWith("/") ? path : "/" + path;

        // POST /api/auth/login
        if (path.equals("/api/auth/login") || path.equals("api/auth/login")) {
            return "POST".equalsIgnoreCase(method);
        }

        // POST /api/auth/registro
        if (path.equals("/api/auth/registro") || path.equals("api/auth/registro")) {
            return "POST".equalsIgnoreCase(method);
        }

        // GET /api/productos (listado)
        if (path.equals("/api/productos") || path.equals("api/productos")) {
            return "GET".equalsIgnoreCase(method);
        }

        // GET /api/productos/{id} (detalle)
        if (path.matches("/?api/productos/\\d+")) {
            return "GET".equalsIgnoreCase(method);
        }

        // GET /api/productos/{id}/tallas
        if (path.matches("/?api/productos/\\d+/tallas")) {
            return "GET".equalsIgnoreCase(method);
        }

        // GET /api/productos/{id}/resenas (ver reseñas es público)
        if (path.matches("/?api/productos/\\d+/resenas")) {
            return "GET".equalsIgnoreCase(method);
        }

        // GET /api/categorias
        if (path.equals("/api/categorias") || path.equals("api/categorias") ||
            path.matches("/?api/categorias(/.*)?")) {
            return "GET".equalsIgnoreCase(method);
        }

        return false;
    }

    /**
     * Determina si una ruta requiere rol de administrador.
     */
    private boolean isAdminRoute(String path) {
        path = path.startsWith("/") ? path : "/" + path;
        return path.startsWith("/api/admin") || path.startsWith("api/admin");
    }

    /**
     * Responde con 401 Unauthorized.
     */
    private void abortUnauthorized(ContainerRequestContext ctx, String message) {
        ctx.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + message + "\", \"code\": 401}")
                    .type("application/json")
                    .build()
        );
    }

    /**
     * Responde con 403 Forbidden.
     */
    private void abortForbidden(ContainerRequestContext ctx) {
        ctx.abortWith(
            Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"No autorizado para esta acción\", \"code\": 403}")
                    .type("application/json")
                    .build()
        );
    }
}
