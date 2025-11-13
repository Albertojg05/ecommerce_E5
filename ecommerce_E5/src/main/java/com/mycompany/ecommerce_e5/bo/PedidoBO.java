/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.PedidoDAO;
import com.mycompany.ecommerce_e5.dominio.*;
import com.mycompany.ecommerce_e5.dominio.enums.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Alberto Jiménez García 252595
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */

public class PedidoBO {
    
    private final PedidoDAO pedidoDAO;
    private final ProductoBO productoBO;
    
    public PedidoBO() {
        this.pedidoDAO = new PedidoDAO();
        this.productoBO = new ProductoBO();
    }
    
    /**
     * Obtener todos los pedidos
     */
    public List<Pedido> obtenerTodos() {
        return pedidoDAO.obtenerTodos();
    }
    
    /**
     * Obtener pedido por ID
     */
    public Pedido obtenerPorId(int id) {
        return pedidoDAO.obtenerPorId(id);
    }
    
    /**
     * Obtener pedidos por usuario
     */
    public List<Pedido> obtenerPorUsuario(int usuarioId) {
        return pedidoDAO.obtenerPorUsuario(usuarioId);
    }
    
    /**
     * Obtener pedidos por estado
     */
    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        return pedidoDAO.obtenerPorEstado(estado.name());
    }
    
    /**
     * Crear nuevo pedido
     */
    public Pedido crearPedido(Usuario usuario, Direccion direccion, 
                              List<DetallePedido> detalles, MetodoPago metodoPago) throws Exception {
        // Validaciones
        if (usuario == null) {
            throw new Exception("El usuario es requerido");
        }
        if (direccion == null) {
            throw new Exception("La dirección de envío es requerida");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new Exception("El pedido debe tener al menos un producto");
        }
        if (metodoPago == null) {
            throw new Exception("El método de pago es requerido");
        }
        
        // Calcular total y verificar stock
        double total = 0;
        for (DetallePedido detalle : detalles) {
            // Verificar disponibilidad
            if (!productoBO.verificarDisponibilidad(
                    detalle.getProducto().getId(), detalle.getCantidad())) {
                throw new Exception(
                    "No hay suficiente stock para: " + detalle.getProducto().getNombre());
            }
            total += detalle.getPrecioUnitario() * detalle.getCantidad();
        }
        
        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setFecha(new Date());
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setUsuario(usuario);
        pedido.setDireccionEnvio(direccion);
        pedido.setDetalles(detalles);
        
        // Crear pago
        Pago pago = new Pago();
        pago.setMonto(total);
        pago.setFecha(new Date());
        pago.setMetodo(metodoPago);
        pago.setEstado(EstadoPago.PENDIENTE);
        pedido.setPago(pago);
        
        // Asociar detalles al pedido
        for (DetallePedido detalle : detalles) {
            detalle.setPedido(pedido);
        }
        
        // Guardar pedido
        Pedido pedidoGuardado = pedidoDAO.guardar(pedido);
        
        // Reducir existencias
        for (DetallePedido detalle : detalles) {
            productoBO.reducirExistencias(
                detalle.getProducto().getId(), detalle.getCantidad());
        }
        
        return pedidoGuardado;
    }
    
    /**
     * Actualizar estado del pedido
     */
    public Pedido actualizarEstado(int pedidoId, EstadoPedido nuevoEstado) throws Exception {
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            throw new Exception("Pedido no encontrado");
        }
        
        pedido.setEstado(nuevoEstado);
        return pedidoDAO.actualizar(pedido);
    }
    
    /**
     * Actualizar estado del pago
     */
    public Pedido actualizarEstadoPago(int pedidoId, EstadoPago nuevoEstado) throws Exception {
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            throw new Exception("Pedido no encontrado");
        }
        
        if (pedido.getPago() != null) {
            pedido.getPago().setEstado(nuevoEstado);
            return pedidoDAO.actualizar(pedido);
        }
        
        throw new Exception("El pedido no tiene información de pago");
    }
    
    /**
     * Generar número único de pedido
     */
    private String generarNumeroPedido() {
        return "PED-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Cancelar pedido
     */
    public void cancelarPedido(int pedidoId) throws Exception {
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            throw new Exception("Pedido no encontrado");
        }
        
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new Exception("No se puede cancelar un pedido ya entregado");
        }
        
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoDAO.actualizar(pedido);
        
        // Restaurar existencias
        for (DetallePedido detalle : pedido.getDetalles()) {
            productoBO.reducirExistencias(
                detalle.getProducto().getId(), -detalle.getCantidad());
        }
    }
}
