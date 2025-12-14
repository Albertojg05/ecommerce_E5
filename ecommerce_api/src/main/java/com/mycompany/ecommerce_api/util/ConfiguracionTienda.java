/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.util;

/**
 * Configuracion centralizada de la tienda.
 * Contiene constantes y parametros configurables del negocio como
 * costos de envio, cantidades minimas y maximas, paginacion, etc.
 * Tambien incluye metodos utilitarios para calculos comunes.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public final class ConfiguracionTienda {

    private ConfiguracionTienda() {
    }

    // ==================== COSTOS DE ENVÍO ====================

    /**
     * Costo de envío estándar en pesos mexicanos.
     */
    public static final double COSTO_ENVIO_ESTANDAR = 200.00;

    /**
     * Costo de envío express en pesos mexicanos.
     */
    public static final double COSTO_ENVIO_EXPRESS = 350.00;

    /**
     * Monto mínimo de compra para envío gratis.
     */
    public static final double MONTO_MINIMO_ENVIO_GRATIS = 1500.00;

    // ==================== CONFIGURACIÓN DE PEDIDOS ====================

    /**
     * Cantidad mínima permitida por producto en el carrito.
     */
    public static final int CANTIDAD_MINIMA_PRODUCTO = 1;

    /**
     * Cantidad máxima permitida por producto en el carrito.
     */
    public static final int CANTIDAD_MAXIMA_PRODUCTO = 10;

    // ==================== CONFIGURACIÓN DE PAGINACIÓN ====================

    /**
     * Número de productos por página en el catálogo.
     */
    public static final int PRODUCTOS_POR_PAGINA = 12;

    /**
     * Número de pedidos por página en el historial.
     */
    public static final int PEDIDOS_POR_PAGINA = 10;

    // ==================== MÉTODOS UTILITARIOS ====================

    /**
     * Calcula el costo de envío basado en el subtotal del pedido.
     *
     * @param subtotal El subtotal del pedido
     * @return El costo de envío (0 si aplica envío gratis)
     */
    public static double calcularCostoEnvio(double subtotal) {
        if (subtotal >= MONTO_MINIMO_ENVIO_GRATIS) {
            return 0.0;
        }
        return COSTO_ENVIO_ESTANDAR;
    }

    /**
     * Calcula el costo de envío con tipo específico.
     *
     * @param subtotal   El subtotal del pedido
     * @param tipoEnvio  Tipo de envío: "estandar" o "express"
     * @return El costo de envío
     */
    public static double calcularCostoEnvio(double subtotal, String tipoEnvio) {
        if (subtotal >= MONTO_MINIMO_ENVIO_GRATIS && !"express".equalsIgnoreCase(tipoEnvio)) {
            return 0.0;
        }

        if ("express".equalsIgnoreCase(tipoEnvio)) {
            return COSTO_ENVIO_EXPRESS;
        }

        return COSTO_ENVIO_ESTANDAR;
    }

    /**
     * Verifica si la cantidad solicitada está dentro de los límites permitidos.
     *
     * @param cantidad La cantidad a verificar
     * @return true si la cantidad es válida
     */
    public static boolean esCantidadValida(int cantidad) {
        return cantidad >= CANTIDAD_MINIMA_PRODUCTO && cantidad <= CANTIDAD_MAXIMA_PRODUCTO;
    }

    /**
     * Calcula cuánto falta para envío gratis.
     *
     * @param subtotal El subtotal actual
     * @return El monto faltante (0 si ya aplica envío gratis)
     */
    public static double faltaParaEnvioGratis(double subtotal) {
        double faltante = MONTO_MINIMO_ENVIO_GRATIS - subtotal;
        return faltante > 0 ? faltante : 0;
    }
}
