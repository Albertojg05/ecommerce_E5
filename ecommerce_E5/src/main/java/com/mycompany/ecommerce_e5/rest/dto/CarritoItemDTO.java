package com.mycompany.ecommerce_e5.rest.dto;

/**
 * DTO para un item del carrito.
 */
public class CarritoItemDTO {
    private int productoId;
    private String nombre;
    private double precio;
    private int cantidad;
    private String imagenUrl;
    private double subtotal;
    private int tallaId;
    private String talla;
    private String color;

    public CarritoItemDTO() {}

    public CarritoItemDTO(int productoId, String nombre, double precio, int cantidad, String imagenUrl) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
        this.subtotal = precio * cantidad;
    }

    public CarritoItemDTO(int productoId, String nombre, double precio, int cantidad, String imagenUrl,
                          int tallaId, String talla, String color) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
        this.subtotal = precio * cantidad;
        this.tallaId = tallaId;
        this.talla = talla;
        this.color = color;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.precio * cantidad;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public int getTallaId() {
        return tallaId;
    }

    public void setTallaId(int tallaId) {
        this.tallaId = tallaId;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
