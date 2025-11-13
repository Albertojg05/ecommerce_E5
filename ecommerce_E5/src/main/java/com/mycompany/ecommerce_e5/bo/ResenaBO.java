/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.ResenaDAO;
import com.mycompany.ecommerce_e5.dominio.Resena;
import java.util.List;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

public class ResenaBO {
    
    private final ResenaDAO resenaDAO;
    
    public ResenaBO() {
        this.resenaDAO = new ResenaDAO();
    }
    
    /**
     * Obtener todas las reseñas
     */
    public List<Resena> obtenerTodas() {
        return resenaDAO.obtenerTodas();
    }
    
    /**
     * Obtener reseña por ID
     */
    public Resena obtenerPorId(int id) {
        return resenaDAO.obtenerPorId(id);
    }
    
    /**
     * Obtener reseñas por producto
     */
    public List<Resena> obtenerPorProducto(int productoId) {
        return resenaDAO.obtenerPorProducto(productoId);
    }
    
    /**
     * Obtener reseñas por usuario
     */
    public List<Resena> obtenerPorUsuario(int usuarioId) {
        return resenaDAO.obtenerPorUsuario(usuarioId);
    }
    
    /**
     * Crear nueva reseña
     */
    public Resena crear(Resena resena) throws Exception {
        // Validaciones
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new Exception("La calificación debe estar entre 1 y 5");
        }
        if (resena.getComentario() == null || resena.getComentario().trim().isEmpty()) {
            throw new Exception("El comentario es requerido");
        }
        if (resena.getUsuario() == null) {
            throw new Exception("El usuario es requerido");
        }
        if (resena.getProducto() == null) {
            throw new Exception("El producto es requerido");
        }
        
        return resenaDAO.guardar(resena);
    }
    
    /**
     * Eliminar reseña (moderación)
     */
    public void eliminar(int id) throws Exception {
        Resena resena = resenaDAO.obtenerPorId(id);
        if (resena == null) {
            throw new Exception("Reseña no encontrada");
        }
        resenaDAO.eliminar(id);
    }
    
    /**
     * Calcular calificación promedio de un producto
     */
    public double calcularPromedioProducto(int productoId) {
        List<Resena> resenas = resenaDAO.obtenerPorProducto(productoId);
        if (resenas.isEmpty()) {
            return 0.0;
        }
        
        double suma = 0;
        for (Resena resena : resenas) {
            suma += resena.getCalificacion();
        }
        
        return suma / resenas.size();
    }
}
