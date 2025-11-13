/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.ProductoDAO;
import com.mycompany.ecommerce_e5.dominio.Producto;
import java.util.List;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

public class ProductoBO {
    
    private final ProductoDAO productoDAO;
    
    public ProductoBO() {
        this.productoDAO = new ProductoDAO();
    }
    
    /**
     * Obtener todos los productos
     */
    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }
    
    /**
     * Obtener producto por ID
     */
    public Producto obtenerPorId(int id) {
        return productoDAO.obtenerPorId(id);
    }
    
    /**
     * Obtener productos por categoría
     */
    public List<Producto> obtenerPorCategoria(int categoriaId) {
        return productoDAO.obtenerPorCategoria(categoriaId);
    }
    
    /**
     * Buscar productos por nombre
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }
    
    /**
     * Crear nuevo producto
     */
    public Producto crear(Producto producto) throws Exception {
        // Validaciones de negocio
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es requerido");
        }
        if (producto.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a cero");
        }
        if (producto.getExistencias() < 0) {
            throw new Exception("Las existencias no pueden ser negativas");
        }
        if (producto.getCategoria() == null) {
            throw new Exception("La categoría es requerida");
        }
        
        return productoDAO.guardar(producto);
    }
    
    /**
     * Actualizar producto
     */
    public Producto actualizar(Producto producto) throws Exception {
        Producto existente = productoDAO.obtenerPorId(producto.getId());
        if (existente == null) {
            throw new Exception("Producto no encontrado");
        }
        
        // Validaciones
        if (producto.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a cero");
        }
        if (producto.getExistencias() < 0) {
            throw new Exception("Las existencias no pueden ser negativas");
        }
        
        return productoDAO.actualizar(producto);
    }
    
    /**
     * Eliminar producto
     */
    public void eliminar(int id) throws Exception {
        Producto producto = productoDAO.obtenerPorId(id);
        if (producto == null) {
            throw new Exception("Producto no encontrado");
        }
        productoDAO.eliminar(id);
    }
    
    /**
     * Verificar disponibilidad de stock
     */
    public boolean verificarDisponibilidad(int productoId, int cantidad) {
        Producto producto = productoDAO.obtenerPorId(productoId);
        return producto != null && producto.getExistencias() >= cantidad;
    }
    
    /**
     * Reducir existencias del producto
     */
    public void reducirExistencias(int productoId, int cantidad) throws Exception {
        if (!verificarDisponibilidad(productoId, cantidad)) {
            throw new Exception("No hay suficiente stock disponible");
        }
        productoDAO.actualizarExistencias(productoId, -cantidad);
    }
}
