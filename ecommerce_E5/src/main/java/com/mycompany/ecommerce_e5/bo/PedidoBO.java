/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.DireccionDAO;
import com.mycompany.ecommerce_e5.dao.PedidoDAO;
import com.mycompany.ecommerce_e5.dao.ProductoDAO;
import com.mycompany.ecommerce_e5.dominio.*;
import com.mycompany.ecommerce_e5.dominio.enums.EstadoPago;
import com.mycompany.ecommerce_e5.dominio.enums.EstadoPedido;
import com.mycompany.ecommerce_e5.dominio.enums.MetodoPago;
import com.mycompany.ecommerce_e5.util.ConfiguracionTienda;
import com.mycompany.ecommerce_e5.util.Messages;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Clase BO para la logica de negocio de pedidos.
 * Es la clase mas importante del proceso de compra. Se encarga de:
 * - Crear pedidos a partir del carrito
 * - Validar stock disponible
 * - Calcular totales y costo de envio
 * - Gestionar cambios de estado del pedido
 * - Cancelar pedidos y restaurar stock
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class PedidoBO {
    
    private final PedidoDAO pedidoDAO;
    private final ProductoDAO productoDAO;
    private final DireccionDAO direccionDAO;
    
    public PedidoBO() {
        this.pedidoDAO = new PedidoDAO();
        this.productoDAO = new ProductoDAO();
        this.direccionDAO = new DireccionDAO();
    }
    
    public List<Pedido> obtenerTodos() {
        return pedidoDAO.obtenerTodos();
    }
    
    public Pedido obtenerPorId(int id) throws Exception {
        if (id <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        Pedido pedido = pedidoDAO.obtenerPorId(id);
        if (pedido == null) {
            throw new Exception(Messages.ERROR_PEDIDO_NO_ENCONTRADO);
        }
        
        return pedido;
    }
    
    public List<Pedido> obtenerPorUsuario(int usuarioId) {
        return pedidoDAO.obtenerPorUsuario(usuarioId);
    }
    
    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        return pedidoDAO.obtenerPorEstado(estado);
    }

    /**
     * Obtiene los pedidos asociados a una dirección específica.
     * Usado para verificar si una dirección puede ser eliminada (#37).
     */
    public List<Pedido> obtenerPorDireccion(int direccionId) {
        return pedidoDAO.obtenerPorDireccion(direccionId);
    }
    
    public Pedido crearPedido(Usuario usuario, Direccion direccion, 
            List<DetallePedido> carrito, MetodoPago metodoPago) throws Exception {
        
        if (usuario == null || usuario.getId() <= 0) {
            throw new Exception("Usuario inválido");
        }
        
        if (carrito == null || carrito.isEmpty()) {
            throw new Exception(Messages.ERROR_CARRITO_VACIO);
        }
        
        if (metodoPago == null) {
            throw new Exception("Método de pago requerido");
        }
        
        Direccion direccionGuardada;
        if (direccion.getId() <= 0) {
            direccion.setUsuario(usuario);
            direccionGuardada = direccionDAO.guardar(direccion);
        } else {
            direccionGuardada = direccionDAO.obtenerPorId(direccion.getId());
            if (direccionGuardada == null) {
                throw new Exception("Dirección no válida");
            }
        }
        
        double total = 0;
        List<DetallePedido> detallesValidados = new ArrayList<>();
        
        for (DetallePedido item : carrito) {
            Producto productoActual = productoDAO.obtenerPorId(item.getProducto().getId());
            
            if (productoActual == null) {
                throw new Exception("Producto no disponible: " + item.getProducto().getNombre());
            }
            
            if (productoActual.getExistencias() < item.getCantidad()) {
                throw new Exception(Messages.ERROR_STOCK_INSUFICIENTE + 
                    " para: " + productoActual.getNombre() + 
                    ". Disponible: " + productoActual.getExistencias());
            }
            
            item.setPrecioUnitario(productoActual.getPrecio());
            
            total += item.getPrecioUnitario() * item.getCantidad();
            detallesValidados.add(item);
        }

        double costoEnvio = ConfiguracionTienda.calcularCostoEnvio(total);
        total += costoEnvio;
        
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setFecha(new Date());
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setUsuario(usuario);
        pedido.setDireccionEnvio(direccionGuardada);
        
        Pago pago = new Pago();
        pago.setMonto(total);
        pago.setFecha(new Date());
        pago.setMetodo(metodoPago);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setPedido(pedido);
        
        pedido.setPago(pago);
        
        List<DetallePedido> detallesPedido = new ArrayList<>();
        for (DetallePedido item : detallesValidados) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setPedido(pedido);
            detallesPedido.add(detalle);
        }
        
        pedido.setDetalles(detallesPedido);
        
        Pedido pedidoGuardado = pedidoDAO.guardar(pedido);
        
        for (DetallePedido detalle : detallesPedido) {
            productoDAO.actualizarExistencias(
                detalle.getProducto().getId(), 
                -detalle.getCantidad()
            );
        }
        
        return pedidoGuardado;
    }
    
    public void actualizarEstado(int pedidoId, EstadoPedido nuevoEstado) throws Exception {
        if (pedidoId <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        if (nuevoEstado == null) {
            throw new Exception("Estado requerido");
        }
        
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            throw new Exception(Messages.ERROR_PEDIDO_NO_ENCONTRADO);
        }
        
        validarTransicionEstado(pedido.getEstado(), nuevoEstado);
        
        pedido.setEstado(nuevoEstado);
        pedidoDAO.actualizar(pedido);
    }
    
    public void cancelarPedido(int pedidoId) throws Exception {
        if (pedidoId <= 0) {
            throw new Exception(Messages.ERROR_PARAMETROS_INVALIDOS);
        }
        
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            throw new Exception(Messages.ERROR_PEDIDO_NO_ENCONTRADO);
        }
        
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new Exception(Messages.ERROR_PEDIDO_NO_CANCELABLE);
        }
        
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            return;
        }
        
        for (DetallePedido detalle : pedido.getDetalles()) {
            productoDAO.actualizarExistencias(
                detalle.getProducto().getId(), 
                detalle.getCantidad()
            );
        }
        
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoDAO.actualizar(pedido);
    }
    
    private String generarNumeroPedido() {
        int year = java.time.Year.now().getValue();
        int random = (int) (Math.random() * 900) + 100; // Número de 3 dígitos (100-999)
        return "PED-" + year + "-" + random;
    }
    
    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) 
            throws Exception {
        
        if (estadoActual == EstadoPedido.CANCELADO) {
            throw new Exception("No se puede modificar un pedido cancelado");
        }
        
        if (estadoActual == EstadoPedido.ENTREGADO && nuevoEstado != EstadoPedido.ENTREGADO) {
            throw new Exception("No se puede modificar un pedido entregado");
        }
        
        if (nuevoEstado == EstadoPedido.ENVIADO && estadoActual != EstadoPedido.PENDIENTE) {
            throw new Exception("Solo se puede enviar un pedido pendiente");
        }
        
        if (nuevoEstado == EstadoPedido.ENTREGADO && estadoActual != EstadoPedido.ENVIADO) {
            throw new Exception("Solo se puede entregar un pedido enviado");
        }
    }
}