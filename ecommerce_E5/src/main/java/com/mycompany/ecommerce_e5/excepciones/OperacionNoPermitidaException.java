/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.excepciones;

/**
 * Excepcion lanzada cuando una operacion no esta permitida por reglas de negocio.
 * Por ejemplo: intentar cancelar un pedido ya entregado, eliminar una categoria
 * con productos asociados, etc.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class OperacionNoPermitidaException extends BusinessException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
