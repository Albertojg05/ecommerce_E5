/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.CategoriaDAO;
import com.mycompany.ecommerce_e5.dominio.Categoria;
import com.mycompany.ecommerce_e5.util.Messages;
import com.mycompany.ecommerce_e5.util.ValidationUtil;
import java.util.List;

/**
 * Clase BO para la logica de negocio de categorias.
 * Gestiona las categorias de productos con validaciones.
 * No permite eliminar categorias que tengan productos asociados.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class CategoriaBO {
    
    private final CategoriaDAO categoriaDAO;
    
    public CategoriaBO() {
        this.categoriaDAO = new CategoriaDAO();
    }
    
    public List<Categoria> obtenerTodas() {
        return categoriaDAO.obtenerTodas();
    }
    
    public Categoria obtenerPorId(int id) throws Exception {
        if (id <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        Categoria categoria = categoriaDAO.obtenerPorId(id);
        if (categoria == null) {
            throw new Exception(Messages.ERROR_CATEGORIA_NO_ENCONTRADA);
        }
        
        return categoria;
    }
    
    public Categoria crear(Categoria categoria) throws Exception {
        validarCategoria(categoria);
        return categoriaDAO.guardar(categoria);
    }
    
    public Categoria actualizar(Categoria categoria) throws Exception {
        if (categoria == null || categoria.getId() <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        Categoria categoriaExistente = categoriaDAO.obtenerPorId(categoria.getId());
        if (categoriaExistente == null) {
            throw new Exception(Messages.ERROR_CATEGORIA_NO_ENCONTRADA);
        }
        
        validarCategoria(categoria);
        return categoriaDAO.actualizar(categoria);
    }
    
    public void eliminar(int id) throws Exception {
        if (id <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        Categoria categoria = categoriaDAO.obtenerPorId(id);
        if (categoria == null) {
            throw new Exception(Messages.ERROR_CATEGORIA_NO_ENCONTRADA);
        }
        
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            throw new Exception(Messages.ERROR_CATEGORIA_CON_PRODUCTOS);
        }
        
        categoriaDAO.eliminar(id);
    }
    
    private void validarCategoria(Categoria categoria) throws Exception {
        if (categoria == null) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        if (!ValidationUtil.isNotEmpty(categoria.getNombre())) {
            throw new Exception("El nombre de la categoría es requerido");
        }
        
        if (categoria.getNombre().trim().length() < 2) {
            throw new Exception("El nombre debe tener al menos 2 caracteres");
        }
    }
}