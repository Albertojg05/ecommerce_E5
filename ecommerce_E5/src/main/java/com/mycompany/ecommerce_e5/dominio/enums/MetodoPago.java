/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.ecommerce_e5.dominio.enums;

/**
 * Enum que define los metodos de pago disponibles en la tienda.
 * Por ahora se soportan pagos con TARJETA de credito/debito y PAYPAL.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public enum MetodoPago {
    TARJETA("Tarjeta"),
    PAYPAL("PayPal");
    
    private final String descripcion;
    
    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}