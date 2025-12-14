/**
 * Cliente API centralizado para la tienda de ropa.
 * Utiliza Fetch API para comunicarse con el backend REST.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

// Obtener el context path dinamicamente
const contextPath = window.location.pathname.split('/')[1];
const API_BASE = '/' + contextPath + '/api';

/**
 * Realiza una peticion fetch con configuracion predeterminada.
 * @param {string} endpoint - Ruta del endpoint (ej: '/productos')
 * @param {object} options - Opciones adicionales para fetch
 * @returns {Promise} - Promesa con la respuesta JSON
 */
async function apiFetch(endpoint, options = {}) {
    const url = API_BASE + endpoint;

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        credentials: 'same-origin'
    };

    const config = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };

    try {
        const response = await fetch(url, config);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Error en la petición');
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

/**
 * Metodos GET.
 */
const api = {
    get: (endpoint) => apiFetch(endpoint, { method: 'GET' }),

    post: (endpoint, body) => apiFetch(endpoint, {
        method: 'POST',
        body: JSON.stringify(body)
    }),

    put: (endpoint, body) => apiFetch(endpoint, {
        method: 'PUT',
        body: JSON.stringify(body)
    }),

    delete: (endpoint) => apiFetch(endpoint, { method: 'DELETE' })
};

// AUTH

const authApi = {
    /**
     * Inicia sesion del usuario.
     * @param {string} correo
     * @param {string} contrasena
     */
    login: async (correo, contrasena) => {
        return api.post('/auth/login', { correo, contrasena });
    },

    /**
     * Cierra sesion del usuario.
     */
    logout: async () => {
        return api.post('/auth/logout', {});
    },

    /**
     * Verifica el estado de autenticacion.
     */
    status: async () => {
        return api.get('/auth/status');
    }
};

// PRODUCTOS

const productosApi = {
    /**
     * Obtiene lista de productos con filtros opcionales.
     * @param {object} params - { q, categoria, page, size }
     */
    listar: async (params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        const endpoint = queryString ? `/productos?${queryString}` : '/productos';
        return api.get(endpoint);
    },

    /**
     * Busca productos por nombre.
     * @param {string} termino
     */
    buscar: async (termino) => {
        return api.get(`/productos?q=${encodeURIComponent(termino)}`);
    },

    /**
     * Filtra productos por categoria.
     * @param {number} categoriaId
     */
    porCategoria: async (categoriaId) => {
        return api.get(`/productos?categoria=${categoriaId}`);
    },

    /**
     * Obtiene detalle de un producto.
     * @param {number} id
     */
    detalle: async (id) => {
        return api.get(`/productos/${id}`);
    },

    /**
     * Obtiene resenas de un producto.
     * @param {number} productoId
     */
    resenas: async (productoId) => {
        return api.get(`/productos/${productoId}/resenas`);
    },

    /**
     * Agrega una resena a un producto.
     * @param {number} productoId
     * @param {number} calificacion
     * @param {string} comentario
     */
    agregarResena: async (productoId, calificacion, comentario) => {
        return api.post(`/productos/${productoId}/resenas`, { calificacion, comentario });
    }
};

// CATEGORIAS

const categoriasApi = {
    /**
     * Obtiene todas las categorias.
     */
    listar: async () => {
        return api.get('/categorias');
    }
};

// CARRITO

const carritoApi = {
    /**
     * Obtiene el contenido del carrito.
     */
    obtener: async () => {
        return api.get('/carrito');
    },

    /**
     * Agrega un producto al carrito.
     * @param {number} productoId
     * @param {number} tallaId
     * @param {number} cantidad
     */
    agregar: async (productoId, tallaId, cantidad = 1) => {
        return api.post('/carrito', { productoId, tallaId, cantidad });
    },

    /**
     * Actualiza la cantidad de un producto+talla en el carrito.
     * @param {number} productoId
     * @param {number} tallaId
     * @param {number} cantidad
     */
    actualizar: async (productoId, tallaId, cantidad) => {
        return api.put(`/carrito/${productoId}/${tallaId}`, { cantidad });
    },

    /**
     * Elimina un producto+talla del carrito.
     * @param {number} productoId
     * @param {number} tallaId
     */
    eliminar: async (productoId, tallaId) => {
        return api.delete(`/carrito/${productoId}/${tallaId}`);
    },

    /**
     * Vacia el carrito.
     */
    vaciar: async () => {
        return api.delete('/carrito');
    }
};

// PEDIDOS

const pedidosApi = {
    /**
     * Obtiene los pedidos del usuario.
     */
    listar: async () => {
        return api.get('/pedidos');
    },

    /**
     * Obtiene detalle de un pedido.
     * @param {number} id
     */
    detalle: async (id) => {
        return api.get(`/pedidos/${id}`);
    },

    /**
     * Crea un nuevo pedido (checkout).
     * @param {number} direccionId
     * @param {string} metodoPago - 'TARJETA' o 'PAYPAL'
     */
    crear: async (direccionId, metodoPago) => {
        return api.post('/pedidos', { direccionId, metodoPago });
    },

    /**
     * Cancela un pedido.
     * @param {number} id
     */
    cancelar: async (id) => {
        return api.post(`/pedidos/${id}/cancelar`, {});
    }
};

// PERFIL

const perfilApi = {
    /**
     * Obtiene los datos del perfil.
     */
    obtener: async () => {
        return api.get('/perfil');
    },

    /**
     * Actualiza los datos del perfil.
     * @param {object} datos - { nombre, telefono, contrasenaActual, nuevaContrasena }
     */
    actualizar: async (datos) => {
        return api.put('/perfil', datos);
    },

    /**
     * Obtiene las direcciones del usuario.
     */
    direcciones: async () => {
        return api.get('/perfil/direcciones');
    },

    /**
     * Agrega una nueva direccion.
     * @param {object} direccion - { calle, ciudad, estado, codigoPostal }
     */
    agregarDireccion: async (direccion) => {
        return api.post('/perfil/direcciones', direccion);
    }
};

// UTILIDADES UI

/**
 * Muestra un mensaje de notificacion.
 * @param {string} mensaje
 * @param {string} tipo - 'success', 'error', 'info'
 */
function mostrarNotificacion(mensaje, tipo = 'info') {
    // Eliminar notificacion anterior si existe
    const existente = document.querySelector('.notificacion');
    if (existente) {
        existente.remove();
    }

    const div = document.createElement('div');
    div.className = `notificacion notificacion-${tipo}`;
    div.innerHTML = `
        <span>${mensaje}</span>
        <button onclick="this.parentElement.remove()">&times;</button>
    `;

    document.body.appendChild(div);

    // Auto-remover despues de 2 segundos
    setTimeout(() => {
        if (div.parentElement) {
            div.remove();
        }
    }, 2000);
}

/**
 * Formatea un precio a moneda mexicana.
 * @param {number} precio
 */
function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-MX', {
        style: 'currency',
        currency: 'MXN'
    }).format(precio);
}

/**
 * Formatea una fecha.
 * @param {string|Date|number} fecha
 */
function formatearFecha(fecha) {
    if (!fecha) return 'Sin fecha';

    let date;

    // Si es un timestamp (número)
    if (typeof fecha === 'number') {
        date = new Date(fecha);
    }
    // Si es un string con formato ISO o timestamp string
    else if (typeof fecha === 'string') {
        // Intentar parsear directamente
        date = new Date(fecha);

        // Si falla, intentar con formato dd/MM/yyyy
        if (isNaN(date.getTime()) && fecha.includes('/')) {
            const parts = fecha.split('/');
            date = new Date(parts[2], parts[1] - 1, parts[0]);
        }
    }
    // Si ya es Date
    else if (fecha instanceof Date) {
        date = fecha;
    }
    else {
        return 'Fecha inválida';
    }

    // Verificar si la fecha es válida
    if (isNaN(date.getTime())) {
        return 'Fecha inválida';
    }

    return date.toLocaleDateString('es-MX', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * Actualiza el contador del carrito en el header.
 */
async function actualizarContadorCarrito() {
    try {
        const response = await carritoApi.obtener();
        const contador = document.getElementById('carrito-contador');
        if (contador && response.data) {
            contador.textContent = response.data.cantidadItems || 0;
        }
    } catch (error) {
        console.error('Error al actualizar contador del carrito:', error);
    }
}
