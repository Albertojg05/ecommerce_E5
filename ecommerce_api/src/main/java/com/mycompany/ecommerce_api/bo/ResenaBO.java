package com.mycompany.ecommerce_api.bo;

import com.mycompany.ecommerce_api.dao.PedidoDAO;
import com.mycompany.ecommerce_api.dao.ResenaDAO;
import com.mycompany.ecommerce_api.dominio.Resena;
import com.mycompany.ecommerce_api.util.Messages;
import com.mycompany.ecommerce_api.util.ValidationUtil;
import java.util.List;

/**
 * Clase BO para la logica de negocio de resenas.
 * Permite a los clientes crear resenas de productos con una calificacion
 * del 1 al 5 y un comentario opcional. Tambien calcula el promedio
 * de calificaciones de un producto.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class ResenaBO {

    private final ResenaDAO resenaDAO;
    private final PedidoDAO pedidoDAO;

    public ResenaBO() {
        this.resenaDAO = new ResenaDAO();
        this.pedidoDAO = new PedidoDAO();
    }

    public List<Resena> obtenerTodas() {
        return resenaDAO.obtenerTodas();
    }

    public Resena obtenerPorId(int id) throws Exception {
        if (id <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        Resena resena = resenaDAO.obtenerPorId(id);
        if (resena == null) {
            throw new Exception("Reseña no encontrada");
        }

        return resena;
    }

    public List<Resena> obtenerPorProducto(int productoId) {
        return resenaDAO.obtenerPorProducto(productoId);
    }

    public List<Resena> obtenerPorUsuario(int usuarioId) {
        return resenaDAO.obtenerPorUsuario(usuarioId);
    }

    public Resena crear(Resena resena) throws Exception {
        validarResena(resena);

        // Validar que el usuario haya comprado el producto (pedido ENTREGADO)
        int usuarioId = resena.getUsuario().getId();
        int productoId = resena.getProducto().getId();
        if (!pedidoDAO.usuarioComproProducto(usuarioId, productoId)) {
            throw new Exception("Debes comprar este producto para poder escribir una reseña");
        }

        return resenaDAO.guardar(resena);
    }

    /**
     * Verifica si un usuario puede escribir una reseña para un producto.
     * Retorna true si el usuario tiene al menos un pedido ENTREGADO con ese producto.
     */
    public boolean puedeEscribirResena(int usuarioId, int productoId) {
        if (usuarioId <= 0 || productoId <= 0) {
            return false;
        }
        return pedidoDAO.usuarioComproProducto(usuarioId, productoId);
    }

    public void eliminar(int id) throws Exception {
        if (id <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        Resena resena = resenaDAO.obtenerPorId(id);
        if (resena == null) {
            throw new Exception("Reseña no encontrada");
        }

        resenaDAO.eliminar(id);
    }

    public Resena actualizar(int id, int calificacion, String comentario) throws Exception {
        if (id <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        if (calificacion < 1 || calificacion > 5) {
            throw new Exception("La calificación debe estar entre 1 y 5");
        }

        if (comentario != null && !comentario.trim().isEmpty()) {
            if (comentario.trim().length() < 10) {
                throw new Exception("El comentario debe tener al menos 10 caracteres");
            }
        }

        Resena resena = resenaDAO.obtenerPorId(id);
        if (resena == null) {
            throw new Exception("Reseña no encontrada");
        }

        resena.setCalificacion(calificacion);
        resena.setComentario(comentario != null ? comentario.trim() : null);

        return resenaDAO.actualizar(resena);
    }

    public double calcularPromedioProducto(int productoId) {
        List<Resena> resenas = resenaDAO.obtenerPorProducto(productoId);

        if (resenas == null || resenas.isEmpty()) {
            return 0.0;
        }

        int sumaCalificaciones = 0;
        for (Resena resena : resenas) {
            sumaCalificaciones += resena.getCalificacion();
        }

        return (double) sumaCalificaciones / resenas.size();
    }

    private void validarResena(Resena resena) throws Exception {
        if (resena == null) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new Exception("La calificación debe estar entre 1 y 5");
        }

        if (resena.getComentario() != null && !resena.getComentario().trim().isEmpty()) {
            if (resena.getComentario().trim().length() < 10) {
                throw new Exception("El comentario debe tener al menos 10 caracteres");
            }
        }

        if (resena.getUsuario() == null || resena.getUsuario().getId() <= 0) {
            throw new Exception("Usuario requerido");
        }

        if (resena.getProducto() == null || resena.getProducto().getId() <= 0) {
            throw new Exception("Producto requerido");
        }
    }
}
