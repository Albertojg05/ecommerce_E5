package com.mycompany.ecommerce_e5.rest.dto;

import com.mycompany.ecommerce_e5.dominio.ProductoTalla;

/**
 * DTO para datos de talla de producto.
 */
public class ProductoTallaDTO {
    private int id;
    private String talla;
    private int stock;
    private int productoId;

    public ProductoTallaDTO() {}

    public ProductoTallaDTO(ProductoTalla productoTalla) {
        this.id = productoTalla.getId();
        this.talla = productoTalla.getTalla();
        this.stock = productoTalla.getStock();
        if (productoTalla.getProducto() != null) {
            this.productoId = productoTalla.getProducto().getId();
        }
    }

    public ProductoTallaDTO(int id, String talla, int stock) {
        this.id = id;
        this.talla = talla;
        this.stock = stock;
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

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
}
