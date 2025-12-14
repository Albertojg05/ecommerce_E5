package com.mycompany.ecommerce_api.rest.dto;

import com.mycompany.ecommerce_api.dominio.Direccion;

/**
 * DTO para datos de direccion.
 */
public class DireccionDTO {
    private int id;
    private String calle;
    private String ciudad;
    private String estado;
    private String codigoPostal;

    public DireccionDTO() {}

    public DireccionDTO(Direccion direccion) {
        this.id = direccion.getId();
        this.calle = direccion.getCalle();
        this.ciudad = direccion.getCiudad();
        this.estado = direccion.getEstado();
        this.codigoPostal = direccion.getCodigoPostal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getDireccionCompleta() {
        return calle + ", " + ciudad + ", " + estado + " " + codigoPostal;
    }
}
