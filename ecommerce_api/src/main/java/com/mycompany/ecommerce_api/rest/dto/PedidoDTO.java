package com.mycompany.ecommerce_api.rest.dto;

import com.mycompany.ecommerce_api.dominio.DetallePedido;
import com.mycompany.ecommerce_api.dominio.Pedido;
import com.mycompany.ecommerce_api.dominio.enums.EstadoPedido;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DTO para datos de pedido.
 */
public class PedidoDTO {
    private int id;
    private String numeroPedido;
    private Date fecha;
    private String fechaFormateada;
    private double total;
    private EstadoPedido estado;
    private String direccionCompleta;
    private String metodoPago;
    private List<DetallePedidoDTO> detalles;

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "MX"));

    public PedidoDTO() {
        this.detalles = new ArrayList<>();
    }

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.numeroPedido = pedido.getNumeroPedido();
        this.fecha = pedido.getFecha();
        this.fechaFormateada = pedido.getFecha() != null ? DATE_FORMAT.format(pedido.getFecha()) : "Sin fecha";
        this.total = pedido.getTotal();
        this.estado = pedido.getEstado();

        if (pedido.getDireccionEnvio() != null) {
            this.direccionCompleta = pedido.getDireccionEnvio().getCalle() + ", " +
                    pedido.getDireccionEnvio().getCiudad() + ", " +
                    pedido.getDireccionEnvio().getEstado() + " " +
                    pedido.getDireccionEnvio().getCodigoPostal();
        }

        if (pedido.getPago() != null) {
            this.metodoPago = pedido.getPago().getMetodo().name();
        }

        this.detalles = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                this.detalles.add(new DetallePedidoDTO(detalle));
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
        this.fechaFormateada = fecha != null ? DATE_FORMAT.format(fecha) : "Sin fecha";
    }

    public String getFechaFormateada() {
        return fechaFormateada;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getDireccionCompleta() {
        return direccionCompleta;
    }

    public void setDireccionCompleta(String direccionCompleta) {
        this.direccionCompleta = direccionCompleta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDTO> detalles) {
        this.detalles = detalles;
    }
}
