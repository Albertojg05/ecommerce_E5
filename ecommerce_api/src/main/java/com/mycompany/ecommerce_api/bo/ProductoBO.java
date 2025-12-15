package com.mycompany.ecommerce_api.bo;

import com.mycompany.ecommerce_api.dao.ProductoDAO;
import com.mycompany.ecommerce_api.dominio.Producto;
import com.mycompany.ecommerce_api.excepciones.BusinessException;
import com.mycompany.ecommerce_api.excepciones.RecursoNoEncontradoException;
import com.mycompany.ecommerce_api.excepciones.StockInsuficienteException;
import com.mycompany.ecommerce_api.excepciones.ValidacionException;
import com.mycompany.ecommerce_api.util.ValidationUtil;
import java.util.List;

/**
 * Clase BO para la logica de negocio de productos.
 * Maneja las operaciones de productos con validaciones:
 * crear, actualizar, eliminar, buscar y gestionar existencias (stock).
 * Valida que los datos del producto sean correctos antes de guardar.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class ProductoBO {

    private final ProductoDAO productoDAO;

    public ProductoBO() {
        this.productoDAO = new ProductoDAO();
    }

    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }

    /**
     * Obtiene todos los productos con sus tallas cargadas (para admin).
     */
    public List<Producto> obtenerTodosConTallas() {
        return productoDAO.obtenerTodosConTallas();
    }

    public List<Producto> obtenerPaginados(int pagina, int tamano) {
        return productoDAO.obtenerPaginados(pagina, tamano);
    }

    public long contarProductos() {
        return productoDAO.contarProductos();
    }

    /**
     * Cuenta los productos con stock total menor al umbral especificado.
     * @param umbral Cantidad mínima de stock
     * @return Número de productos con stock bajo
     */
    public long contarProductosStockBajo(int umbral) {
        return productoDAO.contarProductosStockBajo(umbral);
    }

    public Producto obtenerPorId(int id) throws BusinessException {
        if (id <= 0) {
            throw new ValidacionException("id", "El ID del producto debe ser mayor a 0");
        }

        Producto producto = productoDAO.obtenerPorId(id);
        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto", id);
        }

        return producto;
    }

    public List<Producto> obtenerPorCategoria(int categoriaId) {
        return productoDAO.obtenerPorCategoria(categoriaId);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }

    public Producto crear(Producto producto) throws BusinessException {
        validarProducto(producto);
        return productoDAO.guardar(producto);
    }

    public Producto actualizar(Producto producto) throws BusinessException {
        if (producto == null || producto.getId() <= 0) {
            throw new ValidacionException("Los parámetros del producto no son válidos");
        }

        Producto productoExistente = productoDAO.obtenerPorId(producto.getId());
        if (productoExistente == null) {
            throw new RecursoNoEncontradoException("Producto", producto.getId());
        }

        validarProducto(producto);
        return productoDAO.actualizar(producto);
    }

    public void eliminar(int id) throws BusinessException {
        if (id <= 0) {
            throw new ValidacionException("id", "El ID del producto debe ser mayor a 0");
        }

        Producto producto = productoDAO.obtenerPorId(id);
        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto", id);
        }

        productoDAO.eliminar(id);
    }

    /**
     * Verifica si hay suficiente stock total para la cantidad solicitada.
     * NOTA: Para validación específica por talla, usar ProductoTallaBO.
     *
     * @param productoId ID del producto
     * @param cantidad   Cantidad solicitada
     * @return true si hay stock suficiente en total
     * @throws BusinessException si el producto no existe
     */
    public boolean verificarStock(int productoId, int cantidad) throws BusinessException {
        Producto producto = obtenerPorId(productoId);
        return producto.getStockTotal() >= cantidad;
    }

    /**
     * Valida el stock total y lanza excepción detallada si es insuficiente.
     * NOTA: Para validación específica por talla, usar ProductoTallaBO.
     *
     * @param productoId ID del producto
     * @param cantidad   Cantidad solicitada
     * @throws StockInsuficienteException si no hay stock suficiente
     * @throws BusinessException          si el producto no existe
     */
    public void validarStock(int productoId, int cantidad) throws BusinessException {
        Producto producto = obtenerPorId(productoId);
        if (producto.getStockTotal() < cantidad) {
            throw new StockInsuficienteException(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getStockTotal(),
                    cantidad
            );
        }
    }

    private void validarProducto(Producto producto) throws ValidacionException {
        if (producto == null) {
            throw new ValidacionException("El producto no puede ser nulo");
        }

        if (!ValidationUtil.isNotEmpty(producto.getNombre())) {
            throw new ValidacionException("nombre", "El nombre del producto es requerido");
        }

        if (producto.getNombre().trim().length() < 3) {
            throw new ValidacionException("nombre", "El nombre debe tener al menos 3 caracteres");
        }

        if (!ValidationUtil.isPositive(producto.getPrecio())) {
            throw new ValidacionException("precio", "El precio debe ser mayor a 0");
        }

        if (producto.getCategoria() == null || producto.getCategoria().getId() <= 0) {
            throw new ValidacionException("categoria", "La categoría es requerida");
        }
    }
}
