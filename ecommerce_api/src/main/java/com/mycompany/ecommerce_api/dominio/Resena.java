/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.dominio;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entidad que representa una resena o comentario de un producto.
 * Los clientes pueden dejar una calificacion (1 a 5 estrellas) y un comentario
 * sobre los productos que han comprado. Cada usuario solo puede dejar
 * una resena por producto.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@Entity
@Table(name = "resena")
public class Resena implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int calificacion;

    @Column(length = 1000)
    private String comentario;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public Resena() {
    }

    public Resena(int calificacion, String comentario, Date fecha, Usuario usuario, Producto producto) {
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.usuario = usuario;
        this.producto = producto;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}