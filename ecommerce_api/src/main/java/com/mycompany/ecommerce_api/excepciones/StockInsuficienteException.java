/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_api.excepciones;

/**
 * Excepcion lanzada cuando no hay suficiente stock de un producto.
 * Guarda informacion detallada: id del producto, nombre, stock disponible
 * y cantidad solicitada. Util para mostrar mensajes claros al usuario
 * sobre cuantas unidades puede comprar.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class StockInsuficienteException extends BusinessException {

    private final int productoId;
    private final String productoNombre;
    private final int stockDisponible;
    private final int cantidadSolicitada;

    public StockInsuficienteException(int productoId, String productoNombre,
            int stockDisponible, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                productoNombre, stockDisponible, cantidadSolicitada));
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
