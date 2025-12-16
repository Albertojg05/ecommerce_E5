package com.mycompany.ecommerce_e5.rest.dto;

import com.mycompany.ecommerce_e5.dominio.Categoria;

/**
 * DTO para datos de categoria.
 */
public class CategoriaDTO {
    private int id;
    private String nombre;

    public CategoriaDTO() {}

    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
