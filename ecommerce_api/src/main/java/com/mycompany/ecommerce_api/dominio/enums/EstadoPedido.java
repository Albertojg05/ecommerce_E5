/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.ecommerce_api.dominio.enums;

/**
 * Enum que define los posibles estados de un pedido.
 * El flujo normal es: PENDIENTE -> ENVIADO -> ENTREGADO
 * Un pedido puede ser CANCELADO si aun no ha sido entregado.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");
    
    private final String descripcion;
    
    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
