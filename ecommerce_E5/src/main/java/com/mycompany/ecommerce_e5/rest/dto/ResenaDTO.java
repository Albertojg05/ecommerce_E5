package com.mycompany.ecommerce_e5.rest.dto;

import com.mycompany.ecommerce_e5.dominio.Resena;
import java.util.Date;

/**
 * DTO para datos de resena.
 */
public class ResenaDTO {
    private int id;
    private int calificacion;
    private String comentario;
    private Date fecha;
    private String usuarioNombre;
    private int productoId;

    public ResenaDTO() {}

    public ResenaDTO(Resena resena) {
        this.id = resena.getId();
        this.calificacion = resena.getCalificacion();
        this.comentario = resena.getComentario();
        this.fecha = resena.getFecha();
        if (resena.getUsuario() != null) {
            this.usuarioNombre = resena.getUsuario().getNombre();
        }
        if (resena.getProducto() != null) {
            this.productoId = resena.getProducto().getId();
        }
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

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
}
