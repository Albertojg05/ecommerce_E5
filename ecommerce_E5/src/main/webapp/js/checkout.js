/**
 * Modulo de checkout.
 * Maneja el proceso de pago.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar checkout si estamos en la pagina
    const checkoutForm = document.getElementById('checkout-form');
    if (checkoutForm) {
        initCheckout();
    }
});

/**
 * Inicializa el checkout.
 */
async function initCheckout() {
    await cargarDatosCheckout();
    initCheckoutForm();
}

/**
 * Carga los datos necesarios para el checkout.
 */
async function cargarDatosCheckout() {
    try {
        // Cargar direcciones
        const direccionesResponse = await perfilApi.direcciones();
        if (direccionesResponse.success && direccionesResponse.data) {
            renderDirecciones(direccionesResponse.data);
        }

        // Cargar resumen del carrito
        const carritoResponse = await carritoApi.obtener();
        if (carritoResponse.success && carritoResponse.data) {
            renderResumenCheckout(carritoResponse.data);
        }
    } catch (error) {
        console.error('Error al cargar datos del checkout:', error);
        mostrarNotificacion('Error al cargar los datos', 'error');
    }
}

/**
 * Renderiza las direcciones disponibles.
 */
const MAX_DIRECCIONES = 3;

function renderDirecciones(direcciones) {
    const container = document.getElementById('direcciones-container');
    if (!container) return;

    if (!direcciones || direcciones.length === 0) {
        container.innerHTML = `
            <p>No tienes direcciones guardadas.</p>
            <button type="button" class="btn btn-secondary" onclick="irAgregarDireccion()">
                Agregar dirección
            </button>
        `;
        return;
    }

    container.innerHTML = `
        <div class="direcciones-grid">
            ${direcciones.map((dir, index) => `
                <label class="direccion-card ${index === 0 ? 'selected' : ''}">
                    <input type="radio" name="direccionId" value="${dir.id}"
                           ${index === 0 ? 'checked' : ''}>
                    <div class="direccion-content">
                        <p class="direccion-linea">${dir.calle}</p>
                        <p class="direccion-linea">${dir.ciudad}</p>
                        <p class="direccion-linea">${dir.estado}</p>
                        <p class="direccion-linea">${dir.codigoPostal}</p>
                    </div>
                </label>
            `).join('')}
        </div>
    `;

    // Solo mostrar botón si no se alcanzó el límite
    if (direcciones.length < MAX_DIRECCIONES) {
        container.innerHTML += `
            <button type="button" class="btn btn-link" onclick="irAgregarDireccion()">
                + Agregar nueva dirección
            </button>
        `;
    }

    // Agregar evento para cambiar clase selected
    container.querySelectorAll('.direccion-card input[type="radio"]').forEach(radio => {
        radio.addEventListener('change', function() {
            container.querySelectorAll('.direccion-card').forEach(card => card.classList.remove('selected'));
            this.closest('.direccion-card').classList.add('selected');
        });
    });
}

/**
 * Renderiza el resumen del pedido.
 */
function renderResumenCheckout(data) {
    const container = document.getElementById('resumen-checkout');
    if (!container) return;

    const { items, subtotal, envio, total } = data;

    container.innerHTML = `
        <h3>Resumen del pedido</h3>
        <div class="resumen-items">
            ${items.map(item => `
                <div class="resumen-item">
                    <span>${item.nombre} x${item.cantidad}</span>
                    <span>${formatearPrecio(item.subtotal)}</span>
                </div>
            `).join('')}
        </div>
        <hr>
        <div class="resumen-linea">
            <span>Subtotal:</span>
            <span>${formatearPrecio(subtotal)}</span>
        </div>
        <div class="resumen-linea">
            <span>Envío:</span>
            <span>${envio === 0 ? 'GRATIS' : formatearPrecio(envio)}</span>
        </div>
        <hr>
        <div class="resumen-linea total">
            <strong>Total:</strong>
            <strong>${formatearPrecio(total)}</strong>
        </div>
    `;
}

/**
 * Inicializa el formulario de checkout.
 */
function initCheckoutForm() {
    const form = document.getElementById('checkout-form');
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const direccionId = document.querySelector('input[name="direccionId"]:checked');
        const metodoPago = document.querySelector('input[name="metodoPago"]:checked');

        if (!direccionId) {
            mostrarNotificacion('Por favor seleccione una dirección de envío', 'error');
            return;
        }

        if (!metodoPago) {
            mostrarNotificacion('Por favor seleccione un método de pago', 'error');
            return;
        }

        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Procesando...';

        try {
            const response = await pedidosApi.crear(
                parseInt(direccionId.value),
                metodoPago.value
            );

            if (response.success) {
                mostrarNotificacion('¡Pedido realizado con éxito!', 'success');
                // Redirigir a confirmacion
                setTimeout(() => {
                    window.location.href = '/' + contextPath + '/cliente/confirmacion?id=' + response.data.id;
                }, 1500);
            } else {
                mostrarNotificacion(response.message || 'Error al procesar el pedido', 'error');
                submitBtn.disabled = false;
                submitBtn.textContent = 'Confirmar pedido';
            }
        } catch (error) {
            mostrarNotificacion(error.message || 'Error al procesar el pedido', 'error');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Confirmar pedido';
        }
    });
}

/**
 * Redirige a la página de editar perfil para agregar una nueva dirección.
 */
function irAgregarDireccion() {
    // Obtener el context path
    const contextPath = window.location.pathname.split('/')[1];
    window.location.href = '/' + contextPath + '/cliente/cuenta?accion=editar&nuevaDireccion=true';
}

/**
 * Cierra el modal.
 */
function cerrarModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
        modal.remove();
    }
}
