package com.mycompany.ecommerce_e5.rest.dto;

/**
 * DTO para crear una resena.
 */
public class ResenaRequest {
    private int calificacion;
    private String comentario;

    public ResenaRequest() {}

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
}
