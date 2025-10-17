/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.dominio;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alberto Jiménez García 252595 
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
@Entity
public class Resena implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int calificacion;
    private String comentario;

    @Temporal(TemporalType.DATE)
    private Date fecha;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
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
