package com.mycompany.ecommerce_api.bo;

import com.mycompany.ecommerce_api.dao.DireccionDAO;
import com.mycompany.ecommerce_api.dao.PedidoDAO;
import com.mycompany.ecommerce_api.dao.ProductoDAO;
import com.mycompany.ecommerce_api.dao.ProductoTallaDAO;
import com.mycompany.ecommerce_api.dominio.*;
import com.mycompany.ecommerce_api.dominio.enums.EstadoPago;
import com.mycompany.ecommerce_api.dominio.enums.EstadoPedido;
import com.mycompany.ecommerce_api.dominio.enums.MetodoPago;
import com.mycompany.ecommerce_api.util.ConfiguracionTienda;
import com.mycompany.ecommerce_api.util.Messages;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private final ProductoTallaDAO productoTallaDAO;
    private final DireccionDAO direccionDAO;

    public PedidoBO() {
        this.pedidoDAO = new PedidoDAO();
        this.productoDAO = new ProductoDAO();
        this.productoTallaDAO = new ProductoTallaDAO();
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

            // Validar stock por talla
            ProductoTalla tallaSeleccionada = item.getProductoTalla();
            if (tallaSeleccionada == null) {
                throw new Exception("Debe seleccionar una talla para: " + productoActual.getNombre());
            }

            ProductoTalla tallaActual = productoTallaDAO.obtenerPorId(tallaSeleccionada.getId());
            if (tallaActual == null) {
                throw new Exception("Talla no disponible para: " + productoActual.getNombre());
            }

            if (tallaActual.getStock() < item.getCantidad()) {
                throw new Exception(Messages.ERROR_STOCK_INSUFICIENTE +
                    " para: " + productoActual.getNombre() +
                    " talla " + tallaActual.getTalla() +
                    ". Disponible: " + tallaActual.getStock());
            }

            item.setPrecioUnitario(productoActual.getPrecio());
            item.setProductoTalla(tallaActual);

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
            detalle.setProductoTalla(item.getProductoTalla());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setPedido(pedido);
            detallesPedido.add(detalle);
        }

        pedido.setDetalles(detallesPedido);

        Pedido pedidoGuardado = pedidoDAO.guardar(pedido);

        // Reducir stock de cada talla
        for (DetallePedido detalle : detallesPedido) {
            if (detalle.getProductoTalla() != null) {
                productoTallaDAO.reducirStock(
                    detalle.getProductoTalla().getId(),
                    detalle.getCantidad()
                );
            }
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

        // Restaurar stock de cada talla
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getProductoTalla() != null) {
                productoTallaDAO.actualizarStock(
                    detalle.getProductoTalla().getId(),
                    detalle.getCantidad()
                );
            }
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
