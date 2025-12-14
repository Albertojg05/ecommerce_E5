/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.excepciones;

/**
 * Excepcion lanzada cuando un recurso no existe en la base de datos.
 * Por ejemplo: producto no encontrado, usuario no existe, pedido inexistente.
 * Guarda el tipo de recurso y el identificador para mensajes mas informativos.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class RecursoNoEncontradoException extends BusinessException {

    private final String recurso;
    private final Object identificador;

    public RecursoNoEncontradoException(String recurso, Object identificador) {
        super(recurso + " no encontrado con identificador: " + identificador);
        this.recurso = recurso;
        this.identificador = identificador;
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
        this.recurso = null;
        this.identificador = null;
    }

    public String getRecurso() {
        return recurso;
    }

    public Object getIdentificador() {
        return identificador;
    }
}
