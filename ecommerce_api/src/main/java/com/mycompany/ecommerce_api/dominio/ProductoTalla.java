package com.mycompany.ecommerce_api.dominio;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entidad que representa una talla disponible para un producto.
 * Cada producto puede tener multiples tallas con stock individual.
 *
 * @author Alberto Jimenez Garcia 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Ali Castro Roman 252191
 */
@Entity
@Table(name = "producto_talla")
public class ProductoTalla implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 20)
    private String talla;

    @Column(nullable = false)
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public ProductoTalla() {
    }

    public ProductoTalla(String talla, int stock, Producto producto) {
        this.talla = talla;
        this.stock = stock;
        this.producto = producto;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
