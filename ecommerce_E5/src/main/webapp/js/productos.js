/**
 * Modulo de productos.
 * Maneja busqueda, filtros, detalle y resenas.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar buscador
    const searchForm = document.getElementById('search-form');
    if (searchForm) {
        initSearchForm(searchForm);
    }

    // Inicializar filtro de categorias
    const categoriaSelect = document.getElementById('categoria-filter');
    if (categoriaSelect) {
        initCategoriaFilter(categoriaSelect);
    }

    // Inicializar contenedor de productos
    const productosContainer = document.getElementById('productos-container');
    if (productosContainer) {
        cargarProductos();
    }

    // Inicializar formulario de resena
    const resenaForm = document.getElementById('resena-form');
    if (resenaForm) {
        initResenaForm(resenaForm);
    }

    // Inicializar botones de agregar al carrito
    initBotonesCarrito();
});

/**
 * Inicializa el formulario de busqueda.
 */
function initSearchForm(form) {
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        const termino = document.getElementById('search-input').value.trim();

        if (termino) {
            await buscarProductos(termino);
        } else {
            await cargarProductos();
        }
    });

    // Busqueda en tiempo real (debounced)
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        let timeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(timeout);
            timeout = setTimeout(async () => {
                const termino = this.value.trim();
                if (termino.length >= 2) {
                    await buscarProductos(termino);
                } else if (termino.length === 0) {
                    await cargarProductos();
                }
            }, 300);
        });
    }
}

/**
 * Inicializa el filtro de categorias.
 */
function initCategoriaFilter(select) {
    // Cargar categorias
    cargarCategorias(select);

    select.addEventListener('change', async function() {
        const categoriaId = this.value;
        if (categoriaId) {
            await filtrarPorCategoria(categoriaId);
        } else {
            await cargarProductos();
        }
    });
}

/**
 * Carga las categorias en el select.
 */
async function cargarCategorias(select) {
    try {
        const response = await categoriasApi.listar();
        if (response.success && response.data) {
            select.innerHTML = '<option value="">Todas las categorías</option>';
            response.data.forEach(categoria => {
                const option = document.createElement('option');
                option.value = categoria.id;
                option.textContent = categoria.nombre;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error al cargar categorías:', error);
    }
}

/**
 * Carga todos los productos.
 */
async function cargarProductos(page = 1) {
    const container = document.getElementById('productos-container');
    if (!container) return;

    mostrarLoading(container);

    try {
        const response = await productosApi.listar({ page, size: 12 });

        if (response.success && response.data) {
            renderProductos(container, response.data.productos);
            renderPaginacion(response.data);
        }
    } catch (error) {
        container.innerHTML = '<p class="error">Error al cargar productos</p>';
        console.error('Error:', error);
    }
}

/**
 * Busca productos por termino.
 */
async function buscarProductos(termino) {
    const container = document.getElementById('productos-container');
    if (!container) return;

    mostrarLoading(container);

    try {
        const response = await productosApi.buscar(termino);

        if (response.success && response.data) {
            const productos = response.data.productos;
            if (productos.length === 0) {
                container.innerHTML = `<p class="no-results">No se encontraron productos para "${termino}"</p>`;
            } else {
                renderProductos(container, productos);
            }
        }
    } catch (error) {
        container.innerHTML = '<p class="error">Error en la búsqueda</p>';
        console.error('Error:', error);
    }
}

/**
 * Filtra productos por categoria.
 */
async function filtrarPorCategoria(categoriaId) {
    const container = document.getElementById('productos-container');
    if (!container) return;

    mostrarLoading(container);

    try {
        const response = await productosApi.porCategoria(categoriaId);

        if (response.success && response.data) {
            const productos = response.data.productos;
            if (productos.length === 0) {
                container.innerHTML = '<p class="no-results">No hay productos en esta categoría</p>';
            } else {
                renderProductos(container, productos);
            }
        }
    } catch (error) {
        container.innerHTML = '<p class="error">Error al filtrar productos</p>';
        console.error('Error:', error);
    }
}

/**
 * Renderiza los productos en el contenedor.
 */
function renderProductos(container, productos) {
    if (!productos || productos.length === 0) {
        container.innerHTML = '<p class="no-results">No hay productos disponibles</p>';
        return;
    }

    container.innerHTML = productos.map(producto => `
        <div class="producto-card" data-id="${producto.id}">
            <div class="producto-imagen">
                <img src="${producto.imagenUrl || '/' + contextPath + '/imgs/default.png'}"
                     alt="${producto.nombre}"
                     onerror="this.src='/${contextPath}/imgs/default.png'">
            </div>
            <div class="producto-info">
                <h3 class="producto-nombre">${producto.nombre}</h3>
                <p class="producto-categoria">${producto.categoriaNombre || ''}</p>
                <p class="producto-precio">${formatearPrecio(producto.precio)}</p>
                <div class="producto-acciones">
                    <a href="/${contextPath}/cliente/productos?accion=detalle&id=${producto.id}"
                       class="btn btn-secondary">Ver detalle</a>
                    <button class="btn btn-primary btn-agregar-carrito"
                            data-id="${producto.id}"
                            ${producto.existencias <= 0 ? 'disabled' : ''}>
                        ${producto.existencias > 0 ? 'Agregar' : 'Agotado'}
                    </button>
                </div>
            </div>
        </div>
    `).join('');

    // Re-inicializar botones de carrito
    initBotonesCarrito();
}

/**
 * Renderiza la paginacion.
 */
function renderPaginacion(data) {
    const container = document.getElementById('paginacion');
    if (!container) return;

    const totalPages = Math.ceil(data.total / data.size);
    const currentPage = data.page;

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let html = '<div class="paginacion">';

    if (currentPage > 1) {
        html += `<button onclick="cargarProductos(${currentPage - 1})" class="btn-pag">Anterior</button>`;
    }

    for (let i = 1; i <= totalPages; i++) {
        html += `<button onclick="cargarProductos(${i})"
                         class="btn-pag ${i === currentPage ? 'active' : ''}">${i}</button>`;
    }

    if (currentPage < totalPages) {
        html += `<button onclick="cargarProductos(${currentPage + 1})" class="btn-pag">Siguiente</button>`;
    }

    html += '</div>';
    container.innerHTML = html;
}

/**
 * Inicializa el formulario de resena.
 */
function initResenaForm(form) {
    const productoId = form.dataset.productoId;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const calificacion = document.querySelector('input[name="calificacion"]:checked');
        const comentario = document.getElementById('comentario').value.trim();

        if (!calificacion) {
            mostrarNotificacion('Por favor seleccione una calificación', 'error');
            return;
        }

        try {
            const response = await productosApi.agregarResena(
                productoId,
                parseInt(calificacion.value),
                comentario
            );

            if (response.success) {
                mostrarNotificacion('Reseña agregada exitosamente', 'success');
                form.reset();
                // Recargar resenas
                await cargarResenas(productoId);
            } else {
                mostrarNotificacion(response.message || 'Error al agregar reseña', 'error');
            }
        } catch (error) {
            mostrarNotificacion(error.message || 'Error al agregar reseña', 'error');
        }
    });
}

/**
 * Carga las resenas de un producto.
 */
async function cargarResenas(productoId) {
    const container = document.getElementById('resenas-container');
    if (!container) return;

    try {
        const response = await productosApi.resenas(productoId);

        if (response.success && response.data) {
            renderResenas(container, response.data);
        }
    } catch (error) {
        console.error('Error al cargar reseñas:', error);
    }
}

/**
 * Renderiza las resenas.
 */
function renderResenas(container, data) {
    const { resenas, promedio, total } = data;

    // Actualizar promedio
    const promedioElement = document.getElementById('promedio-calificacion');
    if (promedioElement) {
        promedioElement.textContent = promedio.toFixed(1);
    }

    const totalElement = document.getElementById('total-resenas');
    if (totalElement) {
        totalElement.textContent = `(${total} reseñas)`;
    }

    if (!resenas || resenas.length === 0) {
        container.innerHTML = '<p>No hay reseñas aún. ¡Sé el primero en opinar!</p>';
        return;
    }

    container.innerHTML = resenas.map(resena => `
        <div class="resena">
            <div class="resena-header">
                <span class="resena-usuario">${resena.usuarioNombre}</span>
                <span class="resena-fecha">${formatearFecha(resena.fecha)}</span>
            </div>
            <div class="resena-calificacion">
                ${'★'.repeat(resena.calificacion)}${'☆'.repeat(5 - resena.calificacion)}
            </div>
            <p class="resena-comentario">${resena.comentario || ''}</p>
        </div>
    `).join('');
}

/**
 * Inicializa los botones de agregar al carrito.
 */
function initBotonesCarrito() {
    // Boton especifico de la pagina de detalle
    const btnDetalle = document.getElementById('btn-agregar-carrito');
    if (btnDetalle && !btnDetalle.dataset.initialized) {
        btnDetalle.dataset.initialized = 'true';
        btnDetalle.addEventListener('click', async function() {
            const productoId = parseInt(this.dataset.id);
            const cantidadInput = document.getElementById('cantidad-producto');
            const cantidad = cantidadInput ? parseInt(cantidadInput.value) : 1;
            await agregarAlCarrito(productoId, cantidad);
        });
    }

    // Botones en listado de productos (cantidad = 1)
    document.querySelectorAll('.btn-agregar-carrito:not(#btn-agregar-carrito)').forEach(btn => {
        if (!btn.dataset.initialized) {
            btn.dataset.initialized = 'true';
            btn.addEventListener('click', async function() {
                const productoId = parseInt(this.dataset.id);
                await agregarAlCarrito(productoId, 1);
            });
        }
    });
}

/**
 * Agrega un producto al carrito.
 */
async function agregarAlCarrito(productoId, cantidad = 1) {
    try {
        const response = await carritoApi.agregar(productoId, cantidad);

        if (response && response.success) {
            mostrarNotificacion('Producto agregado al carrito', 'success');
            actualizarContadorCarrito();
        } else {
            mostrarNotificacion(response?.message || 'Error al agregar al carrito', 'error');
        }
    } catch (error) {
        console.error('Error agregando al carrito:', error);
        mostrarNotificacion(error.message || 'Error al agregar al carrito', 'error');
    }
}

/**
 * Muestra indicador de carga.
 */
function mostrarLoading(container) {
    container.innerHTML = `
        <div class="loading">
            <div class="spinner"></div>
            <p>Cargando productos...</p>
        </div>
    `;
}
