package com.mycompany.ecommerce_e5.rest;

import com.mycompany.ecommerce_e5.bo.CategoriaBO;
import com.mycompany.ecommerce_e5.bo.ProductoBO;
import com.mycompany.ecommerce_e5.bo.ProductoTallaBO;
import com.mycompany.ecommerce_e5.bo.ResenaBO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.dominio.ProductoTalla;
import com.mycompany.ecommerce_e5.dominio.Resena;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.rest.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST para productos.
 * Endpoints: GET /api/productos, GET /api/productos/{id},
 *            GET /api/productos/{id}/resenas, POST /api/productos/{id}/resenas,
 *            GET /api/categorias
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {

    private final ProductoBO productoBO = new ProductoBO();
    private final ProductoTallaBO productoTallaBO = new ProductoTallaBO();
    private final CategoriaBO categoriaBO = new CategoriaBO();
    private final ResenaBO resenaBO = new ResenaBO();

    @Context
    private HttpServletRequest request;

    /**
     * GET /api/productos
     * Lista productos con filtros opcionales.
     * Query params: q (busqueda), categoria (filtro), page, size (paginacion)
     */
    @GET
    public Response getProductos(
            @QueryParam("q") String busqueda,
            @QueryParam("categoria") Integer categoriaId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("12") int size) {

        try {
            List<Producto> productos;

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                productos = productoBO.buscarPorNombre(busqueda);
            } else if (categoriaId != null && categoriaId > 0) {
                productos = productoBO.obtenerPorCategoria(categoriaId);
            } else {
                productos = productoBO.obtenerPaginados(page, size);
            }

            List<ProductoDTO> productosDTO = new ArrayList<>();
            for (Producto p : productos) {
                productosDTO.add(new ProductoDTO(p));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("productos", productosDTO);
            data.put("total", productoBO.contarProductos());
            data.put("page", page);
            data.put("size", size);

            return Response.ok(ApiResponse.ok(data)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener productos: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/productos/{id}
     * Obtiene detalle de un producto.
     */
    @GET
    @Path("/{id}")
    public Response getProducto(@PathParam("id") int id) {
        try {
            Producto producto = productoBO.obtenerPorId(id);
            List<Resena> resenas = resenaBO.obtenerPorProducto(id);
            double promedio = resenaBO.calcularPromedioProducto(id);

            Map<String, Object> data = new HashMap<>();
            data.put("producto", new ProductoDTO(producto));

            List<ResenaDTO> resenasDTO = new ArrayList<>();
            for (Resena r : resenas) {
                resenasDTO.add(new ResenaDTO(r));
            }
            data.put("resenas", resenasDTO);
            data.put("promedioCalificacion", promedio);
            data.put("totalResenas", resenas.size());

            return Response.ok(ApiResponse.ok(data)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Producto no encontrado"))
                    .build();
        }
    }

    /**
     * GET /api/productos/{id}/resenas
     * Obtiene resenas de un producto.
     */
    @GET
    @Path("/{id}/resenas")
    public Response getResenas(@PathParam("id") int productoId) {
        try {
            List<Resena> resenas = resenaBO.obtenerPorProducto(productoId);
            double promedio = resenaBO.calcularPromedioProducto(productoId);

            List<ResenaDTO> resenasDTO = new ArrayList<>();
            for (Resena r : resenas) {
                resenasDTO.add(new ResenaDTO(r));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("resenas", resenasDTO);
            data.put("promedio", promedio);
            data.put("total", resenas.size());

            return Response.ok(ApiResponse.ok(data)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener reseñas: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/productos/{id}/resenas
     * Agrega una resena a un producto.
     */
    @POST
    @Path("/{id}/resenas")
    public Response agregarResena(@PathParam("id") int productoId, ResenaRequest resenaRequest) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Debe iniciar sesión para agregar una reseña"))
                        .build();
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            Producto producto = productoBO.obtenerPorId(productoId);

            Resena resena = new Resena();
            resena.setCalificacion(resenaRequest.getCalificacion());
            resena.setComentario(resenaRequest.getComentario());
            resena.setFecha(new Date());
            resena.setUsuario(usuario);
            resena.setProducto(producto);

            resenaBO.crear(resena);

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.ok("Reseña agregada exitosamente", new ResenaDTO(resena)))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/productos/{id}/tallas
     * Obtiene las tallas disponibles de un producto con su stock.
     */
    @GET
    @Path("/{id}/tallas")
    public Response getTallas(@PathParam("id") int productoId) {
        try {
            List<ProductoTalla> tallas = productoTallaBO.obtenerTallasPorProducto(productoId);

            List<ProductoTallaDTO> tallasDTO = new ArrayList<>();
            for (ProductoTalla t : tallas) {
                tallasDTO.add(new ProductoTallaDTO(t));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("tallas", tallasDTO);
            data.put("total", tallas.size());

            return Response.ok(ApiResponse.ok(data)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error al obtener tallas: " + e.getMessage()))
                    .build();
        }
    }
}
