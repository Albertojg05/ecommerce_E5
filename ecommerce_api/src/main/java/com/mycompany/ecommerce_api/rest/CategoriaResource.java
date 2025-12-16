package com.mycompany.ecommerce_api.rest;

import com.mycompany.ecommerce_api.bo.CategoriaBO;
import com.mycompany.ecommerce_api.dominio.Categoria;
import com.mycompany.ecommerce_api.rest.dto.ApiResponse;
import com.mycompany.ecommerce_api.rest.dto.CategoriaDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * API REST para categorias.
 * Endpoint: GET /api/categorias
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    private final CategoriaBO categoriaBO = new CategoriaBO();

    /**
     * GET /api/categorias
     * Lista todas las categorias.
     */
    @GET
    public Response getCategorias() {
        try {
            List<Categoria> categorias = categoriaBO.obtenerTodas();
            List<CategoriaDTO> categoriasDTO = new ArrayList<>();

            for (Categoria c : categorias) {
                categoriasDTO.add(new CategoriaDTO(c));
            }

            return Response.ok(ApiResponse.ok(categoriasDTO)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener categorías: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/categorias/{id}
     * Obtiene una categoria por ID.
     */
    @GET
    @Path("/{id}")
    public Response getCategoria(@PathParam("id") int id) {
        try {
            Categoria categoria = categoriaBO.obtenerPorId(id);
            if (categoria == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Categoría no encontrada"))
                        .build();
            }
            return Response.ok(ApiResponse.ok(new CategoriaDTO(categoria))).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error: " + e.getMessage()))
                    .build();
        }
    }
}
