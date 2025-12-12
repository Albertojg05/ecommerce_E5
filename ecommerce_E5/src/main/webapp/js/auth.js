/**
 * Modulo de autenticacion.
 * Maneja login y logout de clientes.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar formulario de login si existe
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        initLoginForm(loginForm);
    }

    // Inicializar boton de logout si existe
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        initLogoutBtn(logoutBtn);
    }
});

/**
 * Inicializa el formulario de login.
 */
function initLoginForm(form) {
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const correo = document.getElementById('correo').value.trim();
        const contrasena = document.getElementById('contrasena').value;
        const submitBtn = form.querySelector('button[type="submit"]');
        const errorDiv = document.getElementById('error-message');

        // Validacion basica
        if (!correo || !contrasena) {
            mostrarError(errorDiv, 'Por favor complete todos los campos');
            return;
        }

        // Deshabilitar boton
        submitBtn.disabled = true;
        submitBtn.textContent = 'Iniciando sesión...';

        try {
            const response = await authApi.login(correo, contrasena);

            if (response.success) {
                mostrarNotificacion('Inicio de sesión exitoso', 'success');
                // Redirigir basado en el rol del usuario
                if (response.data && response.data.rol === 'ADMINISTRADOR') {
                    window.location.href = '/' + contextPath + '/admin/dashboard';
                } else {
                    window.location.href = '/' + contextPath + '/';
                }
            } else {
                mostrarError(errorDiv, response.message || 'Error al iniciar sesión');
            }
        } catch (error) {
            mostrarError(errorDiv, error.message || 'Credenciales incorrectas');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Iniciar Sesión';
        }
    });
}

/**
 * Inicializa el boton de logout.
 */
function initLogoutBtn(btn) {
    btn.addEventListener('click', async function(e) {
        e.preventDefault();

        try {
            await authApi.logout();
            mostrarNotificacion('Sesión cerrada', 'success');
            window.location.href = '/' + contextPath + '/login';
        } catch (error) {
            console.error('Error al cerrar sesión:', error);
            // Redirigir de todas formas
            window.location.href = '/' + contextPath + '/login';
        }
    });
}

/**
 * Muestra un mensaje de error.
 */
function mostrarError(element, mensaje) {
    if (element) {
        element.textContent = mensaje;
        element.style.display = 'block';
        element.classList.add('shake');
        setTimeout(() => element.classList.remove('shake'), 500);
    }
}
