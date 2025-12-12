<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login Administrador - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/auth.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/">MiTienda</a>
            </div>
            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>

        <main class="login-container">
            <h1 class="login-title">ADMINISTRADOR</h1>

            <% if (request.getParameter("logout") != null) { %>
            <div style="background-color: #f5f5f5; color: #333333; padding: 12px; border-radius: 4px; margin-bottom: 20px; text-align: center; border: 1px solid #cccccc;">
                Sesión cerrada correctamente
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) {%>
            <div style="background-color: #f5f5f5; color: #333333; padding: 12px; border-radius: 4px; margin-bottom: 20px; text-align: center; border: 1px solid #cccccc;">
                <%= request.getAttribute("error")%>
            </div>
            <% }%>

            <form class="login-form" method="post" action="${pageContext.request.contextPath}/admin/login">
                <div class="form-group">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" 
                           id="correo" 
                           name="correo" 
                           class="form-input" 
                           required
                           value="<%= request.getAttribute("correo") != null ? request.getAttribute("correo") : ""%>">
                </div>

                <div class="form-group">
                    <label for="password">Contraseña</label>
                    <input type="password" 
                           id="password" 
                           name="contrasena" 
                           class="form-input" 
                           required>
                </div>

                <button class="login-btn" type="submit">INGRESAR</button>
            </form>
        </main>

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