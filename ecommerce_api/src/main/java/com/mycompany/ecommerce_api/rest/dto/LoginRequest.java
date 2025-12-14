package com.mycompany.ecommerce_api.rest.dto;

import java.io.Serializable;

/**
 * DTO para la solicitud de login.
 */
public class LoginRequest implements Serializable{
    private String correo;
    private String contrasena;

    public LoginRequest() {
    }

    public LoginRequest(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
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
}
