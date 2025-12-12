/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.excepciones;

/**
 * Excepcion base para errores de logica de negocio.
 * Todas las demas excepciones personalizadas heredan de esta.
 * Se usa para errores que pueden ocurrir durante operaciones normales
 * del sistema (validaciones, reglas de negocio, etc.)
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class BusinessException extends Exception {

    public BusinessException(String mensaje) {
        super(mensaje);
    }

    public BusinessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
