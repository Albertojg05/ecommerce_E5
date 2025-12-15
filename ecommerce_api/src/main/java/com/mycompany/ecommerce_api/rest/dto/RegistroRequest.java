package com.mycompany.ecommerce_api.rest.dto;

import java.io.Serializable;

/**
 * DTO para la solicitud de registro de usuario.
 *
 * @author Alberto Jimenez
 */
public class RegistroRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;

    public RegistroRequest() {
    }

    public RegistroRequest(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
