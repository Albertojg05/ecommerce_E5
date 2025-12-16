/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.util;

/**
 * Clase que centraliza todos los mensajes del sistema.
 * Incluye mensajes de error y exito para mostrar al usuario.
 * Esto facilita mantener consistencia y permite cambiar mensajes
 * en un solo lugar.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class Messages {
    
    public static final String ERROR_INESPERADO = "Error inesperado. Por favor, contacte al administrador.";
    public static final String ERROR_PARAMETROS_INVALIDOS = "Los parámetros proporcionados no son válidos.";
    public static final String ERROR_RECURSO_NO_ENCONTRADO = "El recurso solicitado no existe.";
    
    public static final String ERROR_CAMPO_REQUERIDO = "Este campo es requerido.";
    public static final String ERROR_CORREO_INVALIDO = "El formato del correo es inválido.";
    public static final String ERROR_CONTRASENA_CORTA = "La contraseña debe tener al menos 6 caracteres.";
    public static final String ERROR_CONTRASENAS_NO_COINCIDEN = "Las contraseñas no coinciden.";
    
    public static final String ERROR_CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos.";
    public static final String ERROR_CORREO_DUPLICADO = "El correo electrónico ya está registrado.";
    
    public static final String ERROR_PRODUCTO_NO_ENCONTRADO = "El producto no existe.";
    public static final String ERROR_STOCK_INSUFICIENTE = "No hay suficiente stock disponible.";
    public static final String ERROR_CATEGORIA_NO_ENCONTRADA = "La categoría no existe.";
    public static final String ERROR_CATEGORIA_CON_PRODUCTOS = "No se puede eliminar una categoría que contiene productos.";

    public static final String ERROR_CARRITO_VACIO = "El carrito está vacío.";
    public static final String ERROR_PEDIDO_NO_ENCONTRADO = "El pedido no existe.";
    public static final String ERROR_PEDIDO_NO_CANCELABLE = "No se puede cancelar un pedido ya entregado.";
    
    public static final String EXITO_REGISTRO = "Cuenta creada exitosamente.";
    public static final String EXITO_PRODUCTO_CREADO = "Producto creado exitosamente.";
    public static final String EXITO_PRODUCTO_ACTUALIZADO = "Producto actualizado exitosamente.";
    public static final String EXITO_PRODUCTO_ELIMINADO = "Producto eliminado exitosamente.";
    
    private Messages() {}
}