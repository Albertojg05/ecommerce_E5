<%--
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Pedido Confirmado - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/confirm.css">
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
                <a href="${pageContext.request.contextPath}/cliente/cuenta">Mi Cuenta</a>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>
        <main class="confirmation-page">
            <c:choose>
                <c:when test="${not empty error}">
                    <!-- Error: pedido no encontrado o sin permisos (#29 y #30) -->
                    <div class="confirmation-box error-box">
                        <div class="error-icon" style="color: #dc3545;">
                            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                                <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                            </svg>
                        </div>
                        <h1>Error</h1>
                        <p class="error-message">${error}</p>
                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/cliente/cuenta" class="btn-secondary">Ver mis pedidos</a>
                            <a href="${pageContext.request.contextPath}/cliente/productos" class="btn-primary">Ir a la tienda</a>
                        </div>
                    </div>
                </c:when>
                <c:when test="${not empty pedido}">
                    <!-- Confirmación exitosa -->
                    <div class="confirmation-box">
                        <div class="success-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
                            </svg>
                        </div>
                        <h1>¡Gracias por tu compra!</h1>
                        <p class="order-number">Tu pedido <strong>#${pedido.numeroPedido}</strong> ha sido confirmado.</p>

                        <div class="order-details">
                            <div class="detail-row">
                                <span>Total pagado:</span>
                                <span class="total-amount">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></span>
                            </div>
                            <div class="detail-row">
                                <span>Método de pago:</span>
                                <span>${pedido.pago.metodo}</span>
                            </div>
                            <div class="detail-row">
                                <span>Estado:</span>
                                <span class="status-badge">${pedido.estado}</span>
                            </div>
                        </div>

                        <p class="info-text">Recibirás un correo con los detalles de tu pedido.</p>

                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/cliente/cuenta" class="btn-secondary">Ver mis pedidos</a>
                            <a href="${pageContext.request.contextPath}/cliente/productos" class="btn-primary">Seguir comprando</a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Sin datos -->
                    <div class="confirmation-box error-box">
                        <h1>Pedido no encontrado</h1>
                        <p>No se encontró información del pedido.</p>
                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/cliente/productos" class="btn-primary">Ir a la tienda</a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
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
                    <a href="${pageContext.request.contextPath}/cliente/cuenta">Mi Cuenta</a>
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
