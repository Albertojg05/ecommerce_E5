package com.mycompany.ecommerce_api.rest.dto;

/**
 * DTO para la solicitud de login.
 */
public class LoginRequest {
    private String correo;
    private String contrasena;

    public LoginRequest() {}

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
