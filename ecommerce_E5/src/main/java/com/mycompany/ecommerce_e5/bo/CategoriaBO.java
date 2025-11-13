/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.CategoriaDAO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import java.util.List;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

public class CategoriaBO {
    
    private final CategoriaDAO categoriaDAO;
    
    public CategoriaBO() {
        this.categoriaDAO = new CategoriaDAO();
    }
    
    /**
     * Obtener todas las categorías
     */
    public List<Categoria> obtenerTodas() {
        return categoriaDAO.obtenerTodas();
    }
    
    /**
     * Obtener categoría por ID
     */
    public Categoria obtenerPorId(int id) {
        return categoriaDAO.obtenerPorId(id);
    }
    
    /**
     * Crear nueva categoría
     */
    public Categoria crear(Categoria categoria) throws Exception {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la categoría es requerido");
        }
        return categoriaDAO.guardar(categoria);
    }
    
    /**
     * Actualizar categoría
     */
    public Categoria actualizar(Categoria categoria) throws Exception {
        Categoria existente = categoriaDAO.obtenerPorId(categoria.getId());
        if (existente == null) {
            throw new Exception("Categoría no encontrada");
        }
        return categoriaDAO.actualizar(categoria);
    }
    
    /**
     * Eliminar categoría
     */
    public void eliminar(int id) throws Exception {
        Categoria categoria = categoriaDAO.obtenerPorId(id);
        if (categoria == null) {
            throw new Exception("Categoría no encontrada");
        }
        categoriaDAO.eliminar(id);
    }
}
