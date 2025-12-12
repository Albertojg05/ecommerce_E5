<%--
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Crear Cuenta - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/auth.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index">MiTienda</a></div>
            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>

        <main class="register-container">
            <h1 class="register-title">Crear Cuenta</h1>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form class="register-form" action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-group">
                    <label for="nombre">Nombre Completo</label>
                    <input type="text" id="nombre" name="nombre" class="form-input"
                           value="${nombre}" placeholder="Tu nombre" required minlength="2"
                           pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü\s]+" title="Solo letras y espacios"
                           oninput="this.value = this.value.replace(/[0-9]/g, '')">
                </div>

                <div class="form-group">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" id="correo" name="correo" class="form-input"
                           value="${correo}" placeholder="tu@correo.com" required>
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono (10 dígitos, opcional)</label>
                    <input type="tel" id="telefono" name="telefono" class="form-input"
                           value="${telefono}" placeholder="Número de Teléfono"
                           pattern="[0-9]{10}" maxlength="10" inputmode="numeric"
                           oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                </div>

                <div class="form-group">
                    <label for="contrasena">Contraseña</label>
                    <input type="password" id="contrasena" name="contrasena" class="form-input"
                           placeholder="Mínimo 6 caracteres, incluye letras y números" required minlength="6"
                           pattern="^(?=.*[a-zA-Z])(?=.*[0-9]).{6,}$"
                           title="Mínimo 6 caracteres, debe incluir al menos una letra y un número">
                </div>

                <div class="form-group">
                    <label for="contrasenaConfirm">Confirmar Contraseña</label>
                    <input type="password" id="contrasenaConfirm" name="contrasenaConfirm" class="form-input"
                           placeholder="Repite tu contraseña" required minlength="6">
                </div>

                <button type="submit" class="register-btn">CREAR CUENTA</button>
            </form>

            <p class="login-link">
                ¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/login">Iniciar sesión</a>
            </p>
        </main>

        <footer>
            <div class="footer-content">
                <div class="footer-section">
                    <h3>MiTienda</h3>
                    <p>Tu tienda de moda en línea con los mejores productos y precios.</p>
                </div>
                <div class="footer-section">
                    <h3>Enlaces</h3>
                    <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                    <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
                </div>
                <div class="footer-section">
                    <h3>Contacto</h3>
                    <p>Email: contacto@mitienda.com</p>
                    <p>Tel: (123) 456-7890</p>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2025 MiTienda. Todos los derechos reservados.</p>
            </div>
        </footer>

        <script>
            // Mobile Menu Toggle
            const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
            const mainNav = document.querySelector('.main-nav');
            const overlay = document.querySelector('.mobile-nav-overlay');

            if (mobileMenuBtn) {
                mobileMenuBtn.addEventListener('click', function() {
                    this.classList.toggle('active');
                    mainNav.classList.toggle('active');
                    overlay.classList.toggle('active');
                    document.body.style.overflow = mainNav.classList.contains('active') ? 'hidden' : '';
                });

                overlay.addEventListener('click', function() {
                    mobileMenuBtn.classList.remove('active');
                    mainNav.classList.remove('active');
                    this.classList.remove('active');
                    document.body.style.overflow = '';
                });
            }
        </script>
    </body>
</html>
