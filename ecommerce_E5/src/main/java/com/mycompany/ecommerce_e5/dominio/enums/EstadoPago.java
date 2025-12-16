/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.ecommerce_e5.dominio.enums;

/**
 * Enum que define los posibles estados de un pago.
 * PENDIENTE: El pago esta en proceso de verificacion.
 * APROBADO: El pago fue exitoso.
 * RECHAZADO: El pago no pudo procesarse.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public enum EstadoPago {
    PENDIENTE("Pendiente"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado");
    
    private final String descripcion;
    
    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
