package com.mycompany.ecommerce_api.rest.dto;

import com.mycompany.ecommerce_api.dominio.ProductoTalla;

/**
 * DTO para datos de talla de producto.
 */
public class ProductoTallaDTO {
    private int id;
    private String talla;
    private int stock;

    public ProductoTallaDTO() {}

    public ProductoTallaDTO(ProductoTalla productoTalla) {
        this.id = productoTalla.getId();
        this.talla = productoTalla.getTalla();
        this.stock = productoTalla.getStock();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
