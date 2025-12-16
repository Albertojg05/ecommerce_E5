/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.ecommerce_e5.dominio.enums;

/**
 * Enum que define los tipos de usuario en el sistema.
 * CLIENTE: Usuario normal que puede navegar, comprar productos y dejar resenas.
 * ADMINISTRADOR: Usuario con permisos para gestionar productos, pedidos y resenas.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public enum RolUsuario {
    CLIENTE("Cliente"),
    ADMINISTRADOR("Administrador");
    
    private final String descripcion;
    
    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}