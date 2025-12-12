/**
 * Modulo del carrito de compras.
 * Maneja agregar, eliminar, actualizar cantidades.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar botones del carrito en la pagina JSP
    initBotonesCarritoJSP();

    // Actualizar contador del carrito en el header
    actualizarContadorCarrito();
});

/**
 * Inicializa los botones del carrito renderizado por JSP.
 */
function initBotonesCarritoJSP() {
    // Botones de disminuir cantidad
    document.querySelectorAll('.btn-menos-js').forEach(btn => {
        btn.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            const cantidadActual = parseInt(this.dataset.cantidad);
            if (cantidadActual <= 1) {
                await eliminarDelCarritoJSP(productoId);
            } else {
                await actualizarCantidadJSP(productoId, cantidadActual - 1);
            }
        });
    });

    // Botones de aumentar cantidad
    document.querySelectorAll('.btn-mas-js').forEach(btn => {
        btn.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            const cantidadActual = parseInt(this.dataset.cantidad);
            await actualizarCantidadJSP(productoId, cantidadActual + 1);
        });
    });

    // Botones de eliminar
    document.querySelectorAll('.btn-eliminar-js').forEach(btn => {
        btn.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            await eliminarDelCarritoJSP(productoId);
        });
    });

    // Boton vaciar carrito
    const btnVaciar = document.querySelector('.btn-vaciar-js');
    if (btnVaciar) {
        btnVaciar.addEventListener('click', function() {
            mostrarModalConfirmacion(
                'Vaciar carrito',
                '¿Estás seguro de que deseas vaciar el carrito?',
                'Confirmar',
                async () => {
                    await vaciarCarritoJSP();
                }
            );
        });
    }

    // Inicializar eventos del modal
    initModalEventos();
}

/**
 * Muestra el modal de confirmación personalizado.
 * @param {string} titulo - Título del modal
 * @param {string} mensaje - Mensaje a mostrar
 * @param {string} textoConfirmar - Texto del botón de confirmar
 * @param {Function} onConfirmar - Función a ejecutar si confirma
 */
function mostrarModalConfirmacion(titulo, mensaje, textoConfirmar, onConfirmar) {
    const modal = document.getElementById('modal-confirmar');
    const modalTitulo = document.getElementById('modal-titulo');
    const modalMensaje = document.getElementById('modal-mensaje');
    const btnConfirmar = document.getElementById('modal-confirmar-btn');

    if (!modal) return;

    modalTitulo.textContent = titulo;
    modalMensaje.textContent = mensaje;
    btnConfirmar.textContent = textoConfirmar;

    // Guardar la función de confirmación
    modal.onConfirmar = onConfirmar;

    // Mostrar modal
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('mostrar'), 10);

    // Prevenir scroll del body
    document.body.style.overflow = 'hidden';
}

/**
 * Cierra el modal de confirmación.
 */
function cerrarModal() {
    const modal = document.getElementById('modal-confirmar');
    if (!modal) return;

    modal.classList.remove('mostrar');
    setTimeout(() => {
        modal.style.display = 'none';
    }, 300);

    // Restaurar scroll del body
    document.body.style.overflow = '';
}

/**
 * Inicializa los eventos del modal.
 */
function initModalEventos() {
    const modal = document.getElementById('modal-confirmar');
    const btnCancelar = document.getElementById('modal-cancelar');
    const btnConfirmar = document.getElementById('modal-confirmar-btn');

    if (!modal) return;

    // Cerrar con botón cancelar
    if (btnCancelar) {
        btnCancelar.addEventListener('click', cerrarModal);
    }

    // Confirmar acción
    if (btnConfirmar) {
        btnConfirmar.addEventListener('click', async function() {
            if (modal.onConfirmar) {
                await modal.onConfirmar();
            }
            cerrarModal();
        });
    }

    // Cerrar al hacer clic fuera del modal
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            cerrarModal();
        }
    });

    // Cerrar con tecla Escape
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.classList.contains('mostrar')) {
            cerrarModal();
        }
    });
}

/**
 * Actualiza cantidad usando API y recarga pagina.
 */
async function actualizarCantidadJSP(productoId, nuevaCantidad) {
    try {
        const response = await carritoApi.actualizar(productoId, nuevaCantidad);
        if (response.success) {
            mostrarNotificacion('Cantidad actualizada', 'success');
            location.reload();
        } else {
            mostrarNotificacion(response.message || 'Error al actualizar', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al actualizar cantidad', 'error');
    }
}

/**
 * Elimina producto usando API y recarga pagina.
 */
async function eliminarDelCarritoJSP(productoId) {
    try {
        const response = await carritoApi.eliminar(productoId);
        if (response.success) {
            mostrarNotificacion('Producto eliminado', 'success');
            location.reload();
        } else {
            mostrarNotificacion(response.message || 'Error al eliminar', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al eliminar producto', 'error');
    }
}

/**
 * Vacia carrito usando API y recarga pagina.
 */
async function vaciarCarritoJSP() {
    try {
        const response = await carritoApi.vaciar();
        if (response.success) {
            mostrarNotificacion('Carrito vaciado', 'success');
            location.reload();
        } else {
            mostrarNotificacion(response.message || 'Error al vaciar', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al vaciar carrito', 'error');
    }
}

/**
 * Carga el contenido del carrito.
 */
async function cargarCarrito() {
    const container = document.getElementById('carrito-container');
    if (!container) return;

    container.innerHTML = '<div class="loading"><div class="spinner"></div><p>Cargando carrito...</p></div>';

    try {
        const response = await carritoApi.obtener();

        if (response.success && response.data) {
            renderCarrito(container, response.data);
        } else {
            container.innerHTML = '<p class="carrito-vacio">Error al cargar el carrito</p>';
        }
    } catch (error) {
        container.innerHTML = '<p class="carrito-vacio">Error al cargar el carrito</p>';
        console.error('Error:', error);
    }
}

/**
 * Renderiza el carrito.
 */
function renderCarrito(container, data) {
    const { items, subtotal, envio, total, envioGratisDesde } = data;

    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="carrito-vacio">
                <h2>Tu carrito está vacío</h2>
                <p>Agrega productos para comenzar</p>
                <a href="/${contextPath}/cliente/productos" class="btn btn-primary">Ver productos</a>
            </div>
        `;
        actualizarResumen({ subtotal: 0, envio: 0, total: 0 });
        return;
    }

    container.innerHTML = `
        <div class="carrito-items">
            ${items.map(item => renderCarritoItem(item)).join('')}
        </div>
    `;

    actualizarResumen({ subtotal, envio, total, envioGratisDesde, cantidadItems: items.length });
    initCarritoEventos();
}

/**
 * Renderiza un item del carrito.
 */
function renderCarritoItem(item) {
    return `
        <div class="carrito-item" data-id="${item.productoId}">
            <div class="item-imagen">
                <img src="${item.imagenUrl || '/' + contextPath + '/imgs/default.png'}"
                     alt="${item.nombre}"
                     onerror="this.src='/${contextPath}/imgs/default.png'">
            </div>
            <div class="item-info">
                <h3 class="item-nombre">${item.nombre}</h3>
                <p class="item-precio">${formatearPrecio(item.precio)}</p>
            </div>
            <div class="item-cantidad">
                <button class="btn-cantidad btn-menos" data-id="${item.productoId}">-</button>
                <input type="number" class="input-cantidad"
                       value="${item.cantidad}" min="1"
                       data-id="${item.productoId}">
                <button class="btn-cantidad btn-mas" data-id="${item.productoId}">+</button>
            </div>
            <div class="item-subtotal">
                <span>${formatearPrecio(item.subtotal)}</span>
            </div>
            <button class="btn-eliminar" data-id="${item.productoId}" title="Eliminar">
                &times;
            </button>
        </div>
    `;
}

/**
 * Actualiza el resumen del carrito.
 */
function actualizarResumen(data) {
    const subtotalElement = document.getElementById('carrito-subtotal');
    const envioElement = document.getElementById('carrito-envio');
    const totalElement = document.getElementById('carrito-total');
    const checkoutBtn = document.getElementById('btn-checkout');
    const mensajeEnvio = document.getElementById('mensaje-envio');

    if (subtotalElement) {
        subtotalElement.textContent = formatearPrecio(data.subtotal || 0);
    }

    if (envioElement) {
        envioElement.textContent = data.envio === 0 ? 'GRATIS' : formatearPrecio(data.envio || 0);
    }

    if (totalElement) {
        totalElement.textContent = formatearPrecio(data.total || 0);
    }

    if (checkoutBtn) {
        checkoutBtn.disabled = !data.cantidadItems || data.cantidadItems === 0;
    }

    if (mensajeEnvio && data.envioGratisDesde) {
        if (data.subtotal < data.envioGratisDesde) {
            const falta = data.envioGratisDesde - data.subtotal;
            mensajeEnvio.innerHTML = `¡Agrega ${formatearPrecio(falta)} más para envío gratis!`;
            mensajeEnvio.style.display = 'block';
        } else {
            mensajeEnvio.innerHTML = '¡Tienes envío gratis!';
            mensajeEnvio.classList.add('envio-gratis');
        }
    }
}

/**
 * Inicializa los eventos del carrito.
 */
function initCarritoEventos() {
    // Botones de disminuir cantidad
    document.querySelectorAll('.btn-menos').forEach(btn => {
        btn.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            const input = document.querySelector(`.input-cantidad[data-id="${productoId}"]`);
            const nuevaCantidad = parseInt(input.value) - 1;

            if (nuevaCantidad < 1) {
                await eliminarDelCarrito(productoId);
            } else {
                await actualizarCantidad(productoId, nuevaCantidad);
            }
        });
    });

    // Botones de aumentar cantidad
    document.querySelectorAll('.btn-mas').forEach(btn => {
        btn.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            const input = document.querySelector(`.input-cantidad[data-id="${productoId}"]`);
            const nuevaCantidad = parseInt(input.value) + 1;
            await actualizarCantidad(productoId, nuevaCantidad);
        });
    });

    // Inputs de cantidad
    document.querySelectorAll('.input-cantidad').forEach(input => {
        input.addEventListener('change', async function() {
            const productoId = parseInt(this.dataset.id);
            const nuevaCantidad = parseInt(this.value);

            if (nuevaCantidad < 1) {
                this.value = 1;
                return;
            }

            await actualizarCantidad(productoId, nuevaCantidad);
        });
    });

    // Botones de eliminar
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            await eliminarDelCarrito(productoId);
        });
    });
}

/**
 * Actualiza la cantidad de un producto.
 */
async function actualizarCantidad(productoId, cantidad) {
    try {
        const response = await carritoApi.actualizar(productoId, cantidad);

        if (response.success) {
            await cargarCarrito();
            actualizarContadorCarrito();
        } else {
            mostrarNotificacion(response.message || 'Error al actualizar', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al actualizar cantidad', 'error');
    }
}

/**
 * Elimina un producto del carrito.
 */
async function eliminarDelCarrito(productoId) {
    try {
        const response = await carritoApi.eliminar(productoId);

        if (response.success) {
            mostrarNotificacion('Producto eliminado del carrito', 'success');
            await cargarCarrito();
            actualizarContadorCarrito();
        } else {
            mostrarNotificacion(response.message || 'Error al eliminar', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al eliminar producto', 'error');
    }
}

/**
 * Vacia todo el carrito (usa modal de confirmación).
 */
function vaciarCarrito() {
    mostrarModalConfirmacion(
        'Vaciar carrito',
        '¿Estás seguro de que deseas vaciar el carrito?',
        'Confirmar',
        async () => {
            try {
                const response = await carritoApi.vaciar();

                if (response.success) {
                    mostrarNotificacion('Carrito vaciado', 'success');
                    await cargarCarrito();
                    actualizarContadorCarrito();
                }
            } catch (error) {
                mostrarNotificacion('Error al vaciar el carrito', 'error');
            }
        }
    );
}

/**
 * Procede al checkout.
 */
function irAlCheckout() {
    window.location.href = '/' + contextPath + '/cliente/checkout';
}
