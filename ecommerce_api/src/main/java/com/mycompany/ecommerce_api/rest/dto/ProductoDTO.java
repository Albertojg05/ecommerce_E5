package com.mycompany.ecommerce_api.rest.dto;

import com.mycompany.ecommerce_api.dominio.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ProductoTallaDTO> tallas;
    private String color;
    private int categoriaId;
    private String categoriaNombre;

    public ProductoDTO() {
        this.tallas = new ArrayList<>();
    }

    public ProductoDTO(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecio();
        this.imagenUrl = producto.getImagenUrl();
        this.stockTotal = producto.getStockTotal();
        this.color = producto.getColor();

        if (producto.getTallas() != null) {
            this.tallas = producto.getTallas().stream()
                    .map(ProductoTallaDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.tallas = new ArrayList<>();
        }

        if (producto.getCategoria() != null) {
            this.categoriaId = producto.getCategoria().getId();
            this.categoriaNombre = producto.getCategoria().getNombre();
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

    public List<ProductoTallaDTO> getTallas() {
        return tallas;
    }

    public void setTallas(List<ProductoTallaDTO> tallas) {
        this.tallas = tallas;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
