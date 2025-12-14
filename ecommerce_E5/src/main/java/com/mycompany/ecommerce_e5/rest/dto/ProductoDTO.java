package com.mycompany.ecommerce_e5.rest.dto;

import com.mycompany.ecommerce_e5.dominio.Producto;
import com.mycompany.ecommerce_e5.dominio.ProductoTalla;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para datos de producto.
 */
public class ProductoDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenUrl;
    private int stockTotal;
    private String color;
    private int categoriaId;
    private String categoriaNombre;
    private List<ProductoTallaDTO> tallas;

    public ProductoDTO() {}

    public ProductoDTO(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecio();
        this.imagenUrl = producto.getImagenUrl();
        this.color = producto.getColor();
        if (producto.getCategoria() != null) {
            this.categoriaId = producto.getCategoria().getId();
            this.categoriaNombre = producto.getCategoria().getNombre();
        }
        // Cargar tallas si están disponibles
        this.tallas = new ArrayList<>();
        if (producto.getTallas() != null) {
            for (ProductoTalla pt : producto.getTallas()) {
                this.tallas.add(new ProductoTallaDTO(pt));
            }
            this.stockTotal = producto.getStockTotal();
        }
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(int stockTotal) {
        this.stockTotal = stockTotal;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<ProductoTallaDTO> getTallas() {
        return tallas;
    }

    public void setTallas(List<ProductoTallaDTO> tallas) {
        this.tallas = tallas;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }
}
