package com.mycompany.ecommerce_e5.rest.dto;

/**
 * DTO para solicitud de checkout/pago.
 */
public class CheckoutRequest {
    private int direccionId;
    private String metodoPago; // TARJETA o PAYPAL

    public CheckoutRequest() {}

    public int getDireccionId() {
        return direccionId;
    }

    public void setDireccionId(int direccionId) {
        this.direccionId = direccionId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
