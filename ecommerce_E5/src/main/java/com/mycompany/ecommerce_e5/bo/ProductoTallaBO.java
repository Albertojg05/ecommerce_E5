/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.ProductoTallaDAO;
import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.dominio.ProductoTalla;
import com.mycompany.ecommerce_e5.util.Messages;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase BO para la logica de negocio de tallas de productos.
 * Maneja la gestion de stock por talla y validaciones relacionadas.
 *
 * @author Alberto Jimenez Garcia 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Ali Castro Roman 252191
 */
public class ProductoTallaBO {

    private final ProductoTallaDAO productoTallaDAO;

    public ProductoTallaBO() {
        this.productoTallaDAO = new ProductoTallaDAO();
    }

    // Orden predefinido para tallas de ropa
    private static final Map<String, Integer> ORDEN_TALLAS = new HashMap<>();
    static {
        ORDEN_TALLAS.put("UNICA", 0);
        ORDEN_TALLAS.put("ÚNICA", 0);
        ORDEN_TALLAS.put("XS", 1);
        ORDEN_TALLAS.put("S", 2);
        ORDEN_TALLAS.put("M", 3);
        ORDEN_TALLAS.put("L", 4);
        ORDEN_TALLAS.put("XL", 5);
        ORDEN_TALLAS.put("XXL", 6);
        ORDEN_TALLAS.put("XXXL", 7);
    }

    /**
     * Obtiene todas las tallas disponibles de un producto, ordenadas correctamente.
     * Orden: Única, XS, S, M, L, XL, XXL, luego numéricas, luego alfabético.
     * @param productoId ID del producto
     * @return Lista de tallas ordenadas
     * @throws Exception si el ID es invalido
     */
    public List<ProductoTalla> obtenerTallasPorProducto(int productoId) throws Exception {
        if (productoId <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        List<ProductoTalla> tallas = productoTallaDAO.obtenerPorProductoId(productoId);

        // Ordenar las tallas correctamente
        tallas.sort((t1, t2) -> {
            String talla1 = t1.getTalla().toUpperCase().trim();
            String talla2 = t2.getTalla().toUpperCase().trim();

            Integer orden1 = ORDEN_TALLAS.get(talla1);
            Integer orden2 = ORDEN_TALLAS.get(talla2);

            // Si ambas son tallas estándar, ordenar por el mapa
            if (orden1 != null && orden2 != null) {
                return orden1.compareTo(orden2);
            }

            // Si solo una es talla estándar, va primero
            if (orden1 != null) return -1;
            if (orden2 != null) return 1;

            // Intentar ordenar numéricamente (para tallas de pantalón: 28, 30, 32...)
            try {
                int num1 = Integer.parseInt(talla1);
                int num2 = Integer.parseInt(talla2);
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                // Si no son números, ordenar alfabéticamente
                return talla1.compareTo(talla2);
            }
        });

        return tallas;
    }

    /**
     * Obtiene una talla por su ID.
     * @param tallaId ID de la talla
     * @return ProductoTalla
     * @throws Exception si no se encuentra
     */
    public ProductoTalla obtenerPorId(int tallaId) throws Exception {
        if (tallaId <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        ProductoTalla talla = productoTallaDAO.obtenerPorId(tallaId);
        if (talla == null) {
            throw new Exception("Talla no encontrada");
        }
        return talla;
    }

    /**
     * Guarda una nueva talla para un producto.
     * @param producto Producto al que pertenece
     * @param tallaNombre Nombre de la talla (ej: "S", "M", "L")
     * @param stock Stock inicial
     * @return Talla guardada
     * @throws Exception si hay error de validacion
     */
    public ProductoTalla guardarTalla(Producto producto, String tallaNombre, int stock) throws Exception {
        if (producto == null || producto.getId() <= 0) {
            throw new Exception("Producto invalido");
        }

        if (tallaNombre == null || tallaNombre.trim().isEmpty()) {
            throw new Exception("La talla es requerida");
        }

        if (stock < 0) {
            throw new Exception("El stock no puede ser negativo");
        }

        // Verificar que no exista la misma talla para este producto
        ProductoTalla existente = productoTallaDAO.obtenerPorProductoYTalla(producto.getId(), tallaNombre);
        if (existente != null) {
            throw new Exception("Ya existe la talla " + tallaNombre + " para este producto");
        }

        ProductoTalla talla = new ProductoTalla(tallaNombre.trim(), stock, producto);
        return productoTallaDAO.guardar(talla);
    }

    /**
     * Actualiza el stock de una talla existente.
     * @param tallaId ID de la talla
     * @param nuevoStock Nuevo valor de stock
     * @return Talla actualizada
     * @throws Exception si hay error
     */
    public ProductoTalla actualizarStock(int tallaId, int nuevoStock) throws Exception {
        if (tallaId <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        if (nuevoStock < 0) {
            throw new Exception("El stock no puede ser negativo");
        }

        ProductoTalla talla = productoTallaDAO.obtenerPorId(tallaId);
        if (talla == null) {
            throw new Exception("Talla no encontrada");
        }

        talla.setStock(nuevoStock);
        return productoTallaDAO.actualizar(talla);
    }

    /**
     * Reduce el stock de una talla (usado en compras).
     * @param tallaId ID de la talla
     * @param cantidad Cantidad a reducir
     * @throws Exception si no hay stock suficiente
     */
    public void reducirStock(int tallaId, int cantidad) throws Exception {
        if (tallaId <= 0 || cantidad <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        ProductoTalla talla = productoTallaDAO.obtenerPorId(tallaId);
        if (talla == null) {
            throw new Exception("Talla no encontrada");
        }

        if (talla.getStock() < cantidad) {
            throw new Exception(Messages.ERROR_STOCK_INSUFICIENTE +
                " para talla " + talla.getTalla() +
                ". Disponible: " + talla.getStock());
        }

        boolean exito = productoTallaDAO.reducirStock(tallaId, cantidad);
        if (!exito) {
            throw new Exception("Error al reducir stock");
        }
    }

    /**
     * Restaura el stock de una talla (usado al cancelar pedidos).
     * @param tallaId ID de la talla
     * @param cantidad Cantidad a restaurar
     * @throws Exception si hay error
     */
    public void restaurarStock(int tallaId, int cantidad) throws Exception {
        if (tallaId <= 0 || cantidad <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        productoTallaDAO.actualizarStock(tallaId, cantidad);
    }

    /**
     * Elimina una talla de un producto.
     * @param tallaId ID de la talla
     * @throws Exception si hay error
     */
    public void eliminarTalla(int tallaId) throws Exception {
        if (tallaId <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }

        productoTallaDAO.eliminar(tallaId);
    }

    /**
     * Verifica si hay stock disponible para una talla.
     * @param tallaId ID de la talla
     * @param cantidad Cantidad requerida
     * @return true si hay stock suficiente
     */
    public boolean hayStockDisponible(int tallaId, int cantidad) {
        if (tallaId <= 0 || cantidad <= 0) {
            return false;
        }

        ProductoTalla talla = productoTallaDAO.obtenerPorId(tallaId);
        return talla != null && talla.getStock() >= cantidad;
    }

    /**
     * Actualiza o crea una talla para un producto.
     * Si la talla ya existe, actualiza su stock. Si no existe, la crea.
     * @param producto Producto
     * @param tallaNombre Nombre de la talla
     * @param stock Stock
     * @return Talla actualizada o creada
     * @throws Exception si hay error
     */
    public ProductoTalla guardarOActualizarTalla(Producto producto, String tallaNombre, int stock) throws Exception {
        if (producto == null || producto.getId() <= 0) {
            throw new Exception("Producto invalido");
        }

        if (tallaNombre == null || tallaNombre.trim().isEmpty()) {
            throw new Exception("La talla es requerida");
        }

        if (stock < 0) {
            throw new Exception("El stock no puede ser negativo");
        }

        ProductoTalla existente = productoTallaDAO.obtenerPorProductoYTalla(producto.getId(), tallaNombre);
        if (existente != null) {
            existente.setStock(stock);
            return productoTallaDAO.actualizar(existente);
        } else {
            ProductoTalla nueva = new ProductoTalla(tallaNombre.trim(), stock, producto);
            return productoTallaDAO.guardar(nueva);
        }
    }
}
