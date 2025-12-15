package com.mycompany.ecommerce_api.rest.dto;

import java.io.Serializable;

/**
 * DTO para la respuesta de autenticacion (login/registro).
 * Contiene el token JWT y los datos del usuario.
 *
 * @author Alberto Jimenez
 */
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String token;
    private UsuarioDTO usuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, UsuarioDTO usuario) {
        this.success = true;
        this.token = token;
        this.usuario = usuario;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }
}
