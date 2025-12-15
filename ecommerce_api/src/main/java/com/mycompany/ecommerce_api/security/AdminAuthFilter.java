package com.mycompany.ecommerce_api.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filtro JAX-RS para verificar rol de administrador.
 * Se ejecuta después de JwtAuthFilter (AUTHENTICATION + 1).
 * Verifica que el rol del token sea "ADMIN".
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Provider
@Secured
@AdminOnly
@Priority(Priorities.AUTHENTICATION + 1)
public class AdminAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String rol = (String) requestContext.getProperty("rol");

        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"No autorizado para esta acción\", \"code\": 403}")
                        .type("application/json")
                        .build()
            );
        }
    }
}
