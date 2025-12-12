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
        <title>Checkout - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/checkout.css">
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
        <main class="checkout-page">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <div class="checkout-container">
                <form id="checkout-form" class="shipping-form" action="${pageContext.request.contextPath}/cliente/checkout" method="post">
                    <!-- Token para prevenir doble envío (#26) -->
                    <input type="hidden" name="submitToken" value="<%= System.currentTimeMillis() %>_<%= Math.random() %>">

                    <section class="form-section">
                        <h2>Dirección de Envío</h2>
                        <!-- Contenedor para direcciones existentes (cargado por JS) -->
                        <div id="direcciones-container">
                            <noscript>
                                <!-- Fallback visible cuando JS está deshabilitado (#23) -->
                                <p class="info-text">Ingresa tu dirección de envío:</p>
                            </noscript>
                            <p class="loading-text" id="loading-direcciones">Cargando direcciones...</p>
                        </div>

                        <!-- Formulario para nueva dirección - visible por defecto (#23) -->
                        <div id="nueva-direccion-fallback" class="direccion-form-fallback">
                            <h3>Dirección de envío</h3>
                            <div class="form-group">
                                <label for="calle">Calle y Número *</label>
                                <input type="text" id="calle" name="calle" class="form-input" required
                                       placeholder="Ej: Av. Principal #123">
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="ciudad">Ciudad *</label>
                                    <input type="text" id="ciudad" name="ciudad" class="form-input" required
                                           placeholder="Ej: Ciudad Obregón">
                                </div>
                                <div class="form-group">
                                    <label for="estado">Estado *</label>
                                    <input type="text" id="estado" name="estado" class="form-input" required
                                           placeholder="Ej: Sonora">
                                </div>
                            </div>
                            <div class="form-group form-group-small">
                                <label for="codigoPostal">Código Postal *</label>
                                <input type="text" id="codigoPostal" name="codigoPostal" class="form-input" required
                                       pattern="[0-9]{5}" maxlength="5" inputmode="numeric"
                                       placeholder="Ej: 85000"
                                       oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                            </div>
                        </div>
                    </section>

                    <section class="form-section">
                        <h2>Método de Pago</h2>
                        <div class="payment-options">
                            <c:forEach var="m" items="${metodosPago}" varStatus="status">
                                <label class="payment-option">
                                    <input type="radio" name="metodoPago" value="${m}" ${status.first ? 'checked' : ''}>
                                    <span class="payment-label">${m}</span>
                                </label>
                            </c:forEach>
                        </div>
                    </section>

                    <button type="submit" class="checkout-btn" id="btn-confirmar">CONFIRMAR COMPRA</button>
                </form>

                <aside class="order-summary">
                    <div id="resumen-checkout" class="summary-box">
                        <h2>Resumen del Pedido</h2>
                        <div class="order-items">
                            <c:forEach var="item" items="${sessionScope.carrito}">
                                <div class="order-item">
                                    <img src="${pageContext.request.contextPath}/${not empty item.producto.imagenUrl ? item.producto.imagenUrl : 'imgs/default.png'}" alt="${item.producto.nombre}">
                                    <div class="order-item-info">
                                        <span class="item-name">${item.producto.nombre}</span>
                                        <span class="item-qty">Cant: ${item.cantidad}</span>
                                    </div>
                                    <span class="item-price">$<fmt:formatNumber value="${item.precioUnitario * item.cantidad}" pattern="#,##0.00"/></span>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="summary-line">
                            <span>Subtotal</span>
                            <span>$<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                        </div>
                        <div class="summary-line">
                            <span>Envío</span>
                            <c:choose>
                                <c:when test="${costoEnvio == 0}">
                                    <strong>Gratis</strong>
                                </c:when>
                                <c:otherwise>
                                    <span>$<fmt:formatNumber value="${costoEnvio}" pattern="#,##0.00"/></span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="summary-total">
                            <span>Total</span>
                            <span>$<fmt:formatNumber value="${total}" pattern="#,##0.00"/></span>
                        </div>
                    </div>
                </aside>
            </div>
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

            // Protección contra doble clic (#26)
            const checkoutForm = document.getElementById('checkout-form');
            const btnConfirmar = document.getElementById('btn-confirmar');
            let formSubmitted = false;

            if (checkoutForm && btnConfirmar) {
                checkoutForm.addEventListener('submit', function(e) {
                    if (formSubmitted) {
                        e.preventDefault();
                        return false;
                    }
                    formSubmitted = true;
                    btnConfirmar.disabled = true;
                    btnConfirmar.textContent = 'PROCESANDO...';
                    btnConfirmar.style.opacity = '0.7';
                    btnConfirmar.style.cursor = 'not-allowed';
                });
            }
        </script>

        <!-- Scripts API REST -->
        <script src="${pageContext.request.contextPath}/js/api.js"></script>
        <script src="${pageContext.request.contextPath}/js/checkout.js"></script>
    </body>
</html>