/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.excepciones;

/**
 * Excepcion lanzada cuando los datos de entrada no son validos.
 * Por ejemplo: correo mal formado, contrasena muy corta, campos vacios, etc.
 * Puede incluir el nombre del campo que fallo para mostrar mensajes mas claros.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class ValidacionException extends BusinessException {

    private final String campo;

    public ValidacionException(String mensaje) {
        super(mensaje);
        this.campo = null;
    }

    public ValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
