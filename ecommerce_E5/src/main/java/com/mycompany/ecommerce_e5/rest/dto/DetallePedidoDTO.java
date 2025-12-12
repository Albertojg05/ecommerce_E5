package com.mycompany.ecommerce_e5.rest.dto;

import com.mycompany.ecommerce_e5.dominio.DetallePedido;

/**
 * DTO para detalle de pedido.
 */
public class DetallePedidoDTO {
    private int productoId;
    private String productoNombre;
    private String productoImagen;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetallePedidoDTO() {}

    public DetallePedidoDTO(DetallePedido detalle) {
        if (detalle.getProducto() != null) {
            this.productoId = detalle.getProducto().getId();
            this.productoNombre = detalle.getProducto().getNombre();
            this.productoImagen = detalle.getProducto().getImagenUrl();
        }
        this.cantidad = detalle.getCantidad();
        this.precioUnitario = detalle.getPrecioUnitario();
        this.subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getProductoImagen() {
        return productoImagen;
    }

    public void setProductoImagen(String productoImagen) {
        this.productoImagen = productoImagen;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
