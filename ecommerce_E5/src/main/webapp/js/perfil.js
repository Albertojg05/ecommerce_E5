/**
 * Modulo de perfil de usuario.
 * Maneja visualizacion y edicion del perfil.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar perfil si estamos en la pagina
    const perfilContainer = document.getElementById('perfil-container');
    if (perfilContainer) {
        cargarPerfil();
    }

    // Inicializar formulario de edicion
    const perfilForm = document.getElementById('perfil-form');
    if (perfilForm) {
        initPerfilForm(perfilForm);
    }
});

/**
 * Carga los datos del perfil.
 */
async function cargarPerfil() {
    const container = document.getElementById('perfil-container');
    if (!container) return;

    try {
        const response = await perfilApi.obtener();

        if (response.success && response.data) {
            renderPerfil(container, response.data);
        } else {
            container.innerHTML = '<p>Error al cargar el perfil</p>';
        }
    } catch (error) {
        container.innerHTML = '<p>Debe iniciar sesión para ver su perfil</p>';
        console.error('Error:', error);
    }
}

/**
 * Renderiza los datos del perfil.
 */
function renderPerfil(container, data) {
    const { usuario, direcciones } = data;

    container.innerHTML = `
        <div class="perfil-info">
            <h2>Mis datos</h2>
            <div class="perfil-datos">
                <p><strong>Nombre:</strong> <span id="perfil-nombre">${usuario.nombre}</span></p>
                <p><strong>Correo:</strong> <span id="perfil-correo">${usuario.correo}</span></p>
                <p><strong>Teléfono:</strong> <span id="perfil-telefono">${usuario.telefono || 'No registrado'}</span></p>
            </div>
            <button class="btn btn-secondary" onclick="mostrarFormEditar()">Editar perfil</button>
        </div>

        <div class="perfil-direcciones">
            <h2>Mis direcciones</h2>
            <div id="direcciones-lista">
                ${renderDirecciones(direcciones)}
            </div>
            <button class="btn btn-secondary" onclick="mostrarFormDireccion()">
                + Agregar dirección
            </button>
        </div>

        <div class="perfil-acciones">
            <a href="/${contextPath}/cliente/cuenta" class="btn btn-primary">
                Ver mis pedidos
            </a>
        </div>
    `;
}

/**
 * Renderiza las direcciones.
 */
function renderDirecciones(direcciones) {
    if (!direcciones || direcciones.length === 0) {
        return '<p class="sin-direcciones">No tienes direcciones guardadas</p>';
    }

    return direcciones.map(dir => `
        <div class="direccion-card">
            <p>${dir.calle}</p>
            <p>${dir.ciudad}, ${dir.estado} ${dir.codigoPostal}</p>
        </div>
    `).join('');
}

/**
 * Muestra el formulario para editar perfil.
 */
function mostrarFormEditar() {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-content">
            <h3>Editar perfil</h3>
            <form id="editar-perfil-form">
                <div class="form-group">
                    <label for="edit-nombre">Nombre:</label>
                    <input type="text" id="edit-nombre" name="nombre"
                           value="${document.getElementById('perfil-nombre')?.textContent || ''}" required>
                </div>
                <div class="form-group">
                    <label for="edit-telefono">Teléfono:</label>
                    <input type="tel" id="edit-telefono" name="telefono"
                           value="${document.getElementById('perfil-telefono')?.textContent === 'No registrado' ? '' : document.getElementById('perfil-telefono')?.textContent || ''}">
                </div>

                <hr>
                <h4>Cambiar contraseña (opcional)</h4>

                <div class="form-group">
                    <label for="contrasena-actual">Contraseña actual:</label>
                    <input type="password" id="contrasena-actual" name="contrasenaActual">
                </div>
                <div class="form-group">
                    <label for="nueva-contrasena">Nueva contraseña:</label>
                    <input type="password" id="nueva-contrasena" name="nuevaContrasena">
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModal()">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Guardar cambios</button>
                </div>
            </form>
        </div>
    `;

    document.body.appendChild(modal);

    document.getElementById('editar-perfil-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        await guardarPerfil();
    });
}

/**
 * Guarda los cambios del perfil.
 */
async function guardarPerfil() {
    const datos = {
        nombre: document.getElementById('edit-nombre').value.trim(),
        telefono: document.getElementById('edit-telefono').value.trim()
    };

    const contrasenaActual = document.getElementById('contrasena-actual').value;
    const nuevaContrasena = document.getElementById('nueva-contrasena').value;

    if (nuevaContrasena) {
        if (!contrasenaActual) {
            mostrarNotificacion('Debe ingresar la contraseña actual', 'error');
            return;
        }
        datos.contrasenaActual = contrasenaActual;
        datos.nuevaContrasena = nuevaContrasena;
    }

    try {
        const response = await perfilApi.actualizar(datos);

        if (response.success) {
            mostrarNotificacion('Perfil actualizado exitosamente', 'success');
            cerrarModal();
            await cargarPerfil();
        } else {
            mostrarNotificacion(response.message || 'Error al actualizar perfil', 'error');
        }
    } catch (error) {
        mostrarNotificacion(error.message || 'Error al actualizar perfil', 'error');
    }
}

/**
 * Inicializa el formulario de perfil (si existe en la pagina).
 */
function initPerfilForm(form) {
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = new FormData(form);
        const datos = Object.fromEntries(formData);

        try {
            const response = await perfilApi.actualizar(datos);

            if (response.success) {
                mostrarNotificacion('Perfil actualizado', 'success');
            } else {
                mostrarNotificacion(response.message || 'Error al actualizar', 'error');
            }
        } catch (error) {
            mostrarNotificacion(error.message || 'Error al actualizar', 'error');
        }
    });
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
