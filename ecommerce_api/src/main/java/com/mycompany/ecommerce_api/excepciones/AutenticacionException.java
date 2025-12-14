/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.excepciones;

/**
 * Excepcion lanzada cuando hay errores de autenticacion.
 * Se usa cuando el correo o contrasena son incorrectos,
 * o cuando un usuario intenta acceder sin permisos.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class AutenticacionException extends BusinessException {

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
