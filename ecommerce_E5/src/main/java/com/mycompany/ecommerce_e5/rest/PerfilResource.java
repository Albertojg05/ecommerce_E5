package com.mycompany.ecommerce_e5.rest;

import com.mycompany.ecommerce_e5.bo.UsuarioBO;
import com.mycompany.ecommerce_e5.dao.DireccionDAO;
import com.mycompany.ecommerce_e5.dominio.Direccion;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.rest.dto.*;
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
 * API REST para perfil de usuario.
 * Endpoints: GET /api/perfil, PUT /api/perfil, GET /api/perfil/direcciones
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Path("/perfil")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilResource {

    private final UsuarioBO usuarioBO = new UsuarioBO();
    private final DireccionDAO direccionDAO = new DireccionDAO();

    @Context
    private HttpServletRequest request;

    /**
     * GET /api/perfil
     * Obtiene los datos del perfil del usuario autenticado.
     */
    @GET
    public Response getPerfil() {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            List<Direccion> direcciones = direccionDAO.obtenerPorUsuario(usuario.getId());

            List<DireccionDTO> direccionesDTO = new ArrayList<>();
            for (Direccion d : direcciones) {
                direccionesDTO.add(new DireccionDTO(d));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("usuario", new UsuarioDTO(usuario));
            data.put("direcciones", direccionesDTO);

            return Response.ok(ApiResponse.ok(data)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener perfil: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * PUT /api/perfil
     * Actualiza los datos del perfil.
     */
    @PUT
    public Response actualizarPerfil(PerfilUpdateRequest updateRequest) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

            // Actualizar datos basicos
            if (updateRequest.getNombre() != null && !updateRequest.getNombre().trim().isEmpty()) {
                usuario.setNombre(updateRequest.getNombre().trim());
            }

            if (updateRequest.getTelefono() != null) {
                usuario.setTelefono(updateRequest.getTelefono().trim());
            }

            // Cambiar contrasena si se proporciona
            if (updateRequest.getNuevaContrasena() != null && !updateRequest.getNuevaContrasena().isEmpty()) {
                if (updateRequest.getContrasenaActual() == null || updateRequest.getContrasenaActual().isEmpty()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponse.error("Debe proporcionar la contraseña actual"))
                            .build();
                }

                try {
                    usuarioBO.cambiarContrasena(
                            usuario.getId(),
                            updateRequest.getContrasenaActual(),
                            updateRequest.getNuevaContrasena()
                    );
                } catch (Exception e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponse.error(e.getMessage()))
                            .build();
                }
            }

            // Guardar cambios
            usuarioBO.actualizar(usuario);

            // Actualizar sesion
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("usuarioNombre", usuario.getNombre());

            return Response.ok(ApiResponse.ok("Perfil actualizado exitosamente", new UsuarioDTO(usuario))).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al actualizar perfil: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/perfil/direcciones
     * Obtiene las direcciones del usuario.
     */
    @GET
    @Path("/direcciones")
    public Response getDirecciones() {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            List<Direccion> direcciones = direccionDAO.obtenerPorUsuario(usuario.getId());

            List<DireccionDTO> direccionesDTO = new ArrayList<>();
            for (Direccion d : direcciones) {
                direccionesDTO.add(new DireccionDTO(d));
            }

            return Response.ok(ApiResponse.ok(direccionesDTO)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener direcciones: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/perfil/direcciones
     * Agrega una nueva direccion.
     */
    @POST
    @Path("/direcciones")
    public Response agregarDireccion(DireccionDTO direccionDTO) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

            Direccion direccion = new Direccion();
            direccion.setCalle(direccionDTO.getCalle());
            direccion.setCiudad(direccionDTO.getCiudad());
            direccion.setEstado(direccionDTO.getEstado());
            direccion.setCodigoPostal(direccionDTO.getCodigoPostal());
            direccion.setUsuario(usuario);

            direccionDAO.guardar(direccion);

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.ok("Dirección agregada exitosamente", new DireccionDTO(direccion)))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al agregar dirección: " + e.getMessage()))
                    .build();
        }
    }
}
