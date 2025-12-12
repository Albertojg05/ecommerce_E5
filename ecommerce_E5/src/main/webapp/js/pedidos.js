/**
 * Modulo de pedidos.
 * Maneja el historial de pedidos del cliente.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar botones de pedidos en la pagina JSP
    initBotonesPedidosJSP();
});

/**
 * Inicializa los botones de pedidos renderizados por JSP.
 */
function initBotonesPedidosJSP() {
    // Botones de ver detalle
    document.querySelectorAll('.btn-ver-pedido-js').forEach(btn => {
        btn.addEventListener('click', async function() {
            const pedidoId = parseInt(this.dataset.id);
            await verDetallePedido(pedidoId);
        });
    });

    // Botones de cancelar pedido
    document.querySelectorAll('.btn-cancelar-pedido-js').forEach(btn => {
        btn.addEventListener('click', function() {
            const pedidoId = parseInt(this.dataset.id);
            mostrarModalConfirmacion(pedidoId);
        });
    });
}

/**
 * Muestra el modal de confirmación para cancelar pedido.
 */
function mostrarModalConfirmacion(pedidoId) {
    // Remover modal existente si hay
    const existente = document.querySelector('.modal-confirmacion');
    if (existente) existente.remove();

    const modal = document.createElement('div');
    modal.className = 'modal modal-confirmacion';
    modal.innerHTML = `
        <div class="modal-content modal-confirmacion-content">
            <h2>Cancelar pedido</h2>
            <p>¿Estás seguro de que deseas cancelar este pedido?</p>
            <div class="modal-confirmacion-actions">
                <button type="button" class="btn-modal-cancelar" onclick="cerrarModalConfirmacion()">Cancelar</button>
                <button type="button" class="btn-modal-confirmar" onclick="confirmarCancelacion(${pedidoId})">Confirmar</button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Cerrar al hacer click fuera
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            cerrarModalConfirmacion();
        }
    });
}

/**
 * Cierra el modal de confirmación.
 */
function cerrarModalConfirmacion() {
    const modal = document.querySelector('.modal-confirmacion');
    if (modal) {
        modal.remove();
    }
}

/**
 * Confirma la cancelación del pedido.
 */
async function confirmarCancelacion(pedidoId) {
    cerrarModalConfirmacion();
    await cancelarPedidoJSP(pedidoId);
}

/**
 * Cancela un pedido usando API y recarga pagina.
 */
async function cancelarPedidoJSP(pedidoId) {
    try {
        const response = await pedidosApi.cancelar(pedidoId);
        if (response.success) {
            mostrarNotificacion('Pedido cancelado exitosamente', 'success');
            location.reload();
        } else {
            mostrarNotificacion(response.message || 'Error al cancelar pedido', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al cancelar pedido', 'error');
    }
}

/**
 * Carga los pedidos del usuario.
 */
async function cargarPedidos() {
    const container = document.getElementById('pedidos-container');
    if (!container) return;

    container.innerHTML = '<div class="loading"><div class="spinner"></div><p>Cargando pedidos...</p></div>';

    try {
        const response = await pedidosApi.listar();

        if (response.success && response.data) {
            renderPedidos(container, response.data);
        } else {
            container.innerHTML = '<p>Error al cargar los pedidos</p>';
        }
    } catch (error) {
        container.innerHTML = '<p>Debe iniciar sesión para ver sus pedidos</p>';
        console.error('Error:', error);
    }
}

/**
 * Renderiza la lista de pedidos.
 */
function renderPedidos(container, pedidos) {
    if (!pedidos || pedidos.length === 0) {
        container.innerHTML = `
            <div class="sin-pedidos">
                <h2>No tienes pedidos aún</h2>
                <p>¡Explora nuestros productos y realiza tu primera compra!</p>
                <a href="/${contextPath}/cliente/productos" class="btn btn-primary">Ver productos</a>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="pedidos-lista">
            ${pedidos.map(pedido => renderPedidoCard(pedido)).join('')}
        </div>
    `;

    // Agregar eventos para ver detalles
    document.querySelectorAll('.btn-ver-pedido').forEach(btn => {
        btn.addEventListener('click', function() {
            const pedidoId = parseInt(this.dataset.id);
            verDetallePedido(pedidoId);
        });
    });
}

/**
 * Renderiza una tarjeta de pedido.
 */
function renderPedidoCard(pedido) {
    const estadoClass = getEstadoClass(pedido.estado);

    return `
        <div class="pedido-card">
            <div class="pedido-header">
                <div class="pedido-numero">
                    <strong>${pedido.numeroPedido}</strong>
                </div>
                <div class="pedido-estado ${estadoClass}">
                    ${pedido.estado}
                </div>
            </div>
            <div class="pedido-info">
                <p><strong>Fecha:</strong> ${formatearFecha(pedido.fecha)}</p>
                <p><strong>Total:</strong> ${formatearPrecio(pedido.total)}</p>
                <p><strong>Método de pago:</strong> ${pedido.metodoPago}</p>
            </div>
            <div class="pedido-acciones">
                <button class="btn btn-secondary btn-ver-pedido" data-id="${pedido.id}">
                    Ver detalles
                </button>
            </div>
        </div>
    `;
}

/**
 * Obtiene la clase CSS segun el estado del pedido.
 */
function getEstadoClass(estado) {
    switch (estado) {
        case 'PENDIENTE':
            return 'estado-pendiente';
        case 'ENVIADO':
            return 'estado-enviado';
        case 'ENTREGADO':
            return 'estado-entregado';
        case 'CANCELADO':
            return 'estado-cancelado';
        default:
            return '';
    }
}

/**
 * Muestra el detalle de un pedido en un modal.
 */
async function verDetallePedido(pedidoId) {
    try {
        const response = await pedidosApi.detalle(pedidoId);

        if (response.success && response.data) {
            mostrarModalPedido(response.data);
        } else {
            mostrarNotificacion('Error al cargar el pedido', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al cargar el pedido', 'error');
    }
}

/**
 * Muestra el modal con los detalles del pedido.
 */
function mostrarModalPedido(pedido) {
    // Remover modal existente si hay
    const existente = document.querySelector('.modal-pedido');
    if (existente) existente.remove();

    const modal = document.createElement('div');
    modal.className = 'modal modal-pedido';
    modal.innerHTML = `
        <div class="modal-content modal-pedido-content">
            <button class="modal-close" onclick="cerrarModalPedido()">&times;</button>
            <h2 class="modal-titulo">Pedido ${pedido.numeroPedido}</h2>

            <div class="pedido-info-section">
                <div class="pedido-info-row">
                    <span class="info-label">Estado:</span>
                    <span class="pedido-estado-badge ${getEstadoClass(pedido.estado)}">${pedido.estado}</span>
                </div>
                <div class="pedido-info-row">
                    <span class="info-label">Fecha:</span>
                    <span>${pedido.fechaFormateada || formatearFecha(pedido.fecha)}</span>
                </div>
                <div class="pedido-info-row">
                    <span class="info-label">Método de pago:</span>
                    <span>${pedido.metodoPago}</span>
                </div>
                <div class="pedido-info-row">
                    <span class="info-label">Dirección:</span>
                    <span>${pedido.direccionCompleta}</span>
                </div>
            </div>

            <div class="pedido-productos-section">
                <h3>Productos</h3>
                <table class="productos-tabla">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Cant.</th>
                            <th>Precio</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${pedido.detalles.map(detalle => `
                            <tr>
                                <td>${detalle.productoNombre}</td>
                                <td>${detalle.cantidad}</td>
                                <td>${formatearPrecio(detalle.precioUnitario)}</td>
                                <td>${formatearPrecio(detalle.subtotal)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>

            <div class="pedido-total-section">
                <span>Total:</span>
                <strong>${formatearPrecio(pedido.total)}</strong>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Cerrar al hacer click fuera
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            cerrarModalPedido();
        }
    });
}

/**
 * Cierra el modal de pedido.
 */
function cerrarModalPedido() {
    const modal = document.querySelector('.modal-pedido');
    if (modal) {
        modal.remove();
    }
}
