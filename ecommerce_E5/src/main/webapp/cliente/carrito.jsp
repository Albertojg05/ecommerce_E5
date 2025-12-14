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
        <title>Carrito - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/cart.css">
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
                <c:choose>
                    <c:when test="${not empty sessionScope.usuarioLogueado}">
                        <a href="${pageContext.request.contextPath}/cliente/cuenta">Mi Cuenta</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">Ingresar</a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>
        <main class="cart-container">
            <!-- Mensajes de feedback -->
            <c:if test="${param.mensaje == 'agregado'}">
                <div class="alert alert-success">Producto agregado al carrito.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    ${error}
                    <c:if test="${not empty stockDisponible}">
                        <br><small>Stock disponible: ${stockDisponible} unidades</small>
                    </c:if>
                </div>
            </c:if>

            <!-- Advertencias de productos eliminados o stock ajustado (#19 y #20) -->
            <c:if test="${not empty advertencias}">
                <div class="alert alert-warning">
                    <strong>Se realizaron ajustes en tu carrito:</strong>
                    <ul class="alert-list">
                        <c:forEach var="adv" items="${advertencias}">
                            <li>${adv}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <!-- Banner de envío gratis -->
            <c:if test="${not empty sessionScope.carrito && faltaEnvioGratis > 0}">
                <div class="shipping-banner">
                    <span>¡Agrega <strong>$<fmt:formatNumber value="${faltaEnvioGratis}" pattern="#,##0.00"/></strong> más para obtener <strong>envío gratis!</strong></span>
                    <div class="shipping-progress">
                        <div class="shipping-progress-bar" style="width: ${(subtotal / montoMinimoEnvioGratis) * 100}%"></div>
                    </div>
                </div>
            </c:if>
            <c:if test="${not empty sessionScope.carrito && faltaEnvioGratis == 0}">
                <div class="shipping-banner shipping-free">
                    <span><strong>¡Felicidades!</strong> Tu pedido tiene <strong>envío gratis</strong></span>
                </div>
            </c:if>

            <section class="cart-items-section">
                <h1>CARRITO</h1>
                <c:choose>
                    <c:when test="${empty sessionScope.carrito}">
                        <div class="empty-cart">
                            <p>Tu carrito está vacío</p>
                            <a href="${pageContext.request.contextPath}/cliente/productos" class="continue-shopping">Ver productos</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="cart-count">${sessionScope.carrito.size()} producto(s) en tu carrito</p>
                        <c:forEach var="item" items="${sessionScope.carrito}">
                            <div class="cart-item" data-producto-id="${item.producto.id}" data-talla-id="${item.productoTalla.id}">
                                <img src="${pageContext.request.contextPath}/${not empty item.producto.imagenUrl ? item.producto.imagenUrl : 'imgs/default.png'}" alt="${item.producto.nombre}">
                                <div class="item-details">
                                    <p class="item-title">${item.producto.nombre}</p>
                                    <c:if test="${not empty item.productoTalla}">
                                        <p class="item-spec">Talla: ${item.productoTalla.talla}</p>
                                    </c:if>
                                    <c:if test="${not empty item.producto.color}">
                                        <p class="item-spec">Color: ${item.producto.color}</p>
                                    </c:if>
                                    <p class="item-price">$<fmt:formatNumber value="${item.precioUnitario}" pattern="#,##0.00"/></p>
                                    <c:set var="stockTalla" value="${item.productoTalla != null ? item.productoTalla.stock : 0}" />
                                    <c:set var="limiteAlcanzado" value="${item.cantidad >= 10 || item.cantidad >= stockTalla}" />
                                    <div class="item-quantity">
                                        <button type="button" class="qty-btn btn-menos-js"
                                                data-id="${item.producto.id}"
                                                data-talla-id="${item.productoTalla.id}"
                                                data-cantidad="${item.cantidad}">−</button>
                                        <span class="qty-value">${item.cantidad}</span>
                                        <button type="button" class="qty-btn btn-mas-js"
                                                data-id="${item.producto.id}"
                                                data-talla-id="${item.productoTalla.id}"
                                                data-cantidad="${item.cantidad}"
                                                data-stock="${stockTalla}"
                                                ${limiteAlcanzado ? 'disabled' : ''}
                                                title="${item.cantidad >= 10 ? 'Máximo 10 unidades por producto' : (item.cantidad >= stockTalla ? 'Stock máximo disponible' : '')}">+</button>
                                    </div>
                                    <p class="item-subtotal">Subtotal: $<fmt:formatNumber value="${item.precioUnitario * item.cantidad}" pattern="#,##0.00"/></p>
                                    <button type="button" class="remove-btn btn-eliminar-js"
                                            data-id="${item.producto.id}"
                                            data-talla-id="${item.productoTalla.id}">Quitar</button>
                                </div>
                            </div>
                        </c:forEach>

                        <!-- Botón vaciar carrito -->
                        <div class="clear-cart-form">
                            <button type="button" class="clear-cart-btn btn-vaciar-js">Vaciar carrito</button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <aside class="cart-summary-section">
                <div class="summary-box">
                    <h2>Resumen del pedido</h2>
                    <div class="summary-line">
                        <span>Subtotal</span>
                        <span id="carrito-subtotal">$<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                    </div>
                    <div class="summary-line">
                        <span>Envío</span>
                        <span id="carrito-envio">
                            <c:choose>
                                <c:when test="${costoEnvio == 0}">Gratis</c:when>
                                <c:otherwise>$<fmt:formatNumber value="${costoEnvio}" pattern="#,##0.00"/></c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="summary-total">
                        <span>Total</span>
                        <span id="carrito-total">$<fmt:formatNumber value="${total}" pattern="#,##0.00"/></span>
                    </div>
                    <c:if test="${not empty sessionScope.carrito}">
                        <a href="${pageContext.request.contextPath}/cliente/checkout" class="checkout-btn">PROCEDER AL PAGO</a>
                        <a href="${pageContext.request.contextPath}/cliente/productos" class="continue-link">Continuar comprando</a>
                    </c:if>
                </div>

                <!-- Información de envío -->
                <div class="shipping-info">
                    <h3>Información de envío</h3>
                    <ul>
                        <li>Envío gratis en compras mayores a $<fmt:formatNumber value="${montoMinimoEnvioGratis}" pattern="#,##0.00"/></li>
                        <li>Envío estándar: 3-5 días hábiles</li>
                        <li>Máximo 10 unidades por producto</li>
                    </ul>
                </div>
            </aside>
        </main>

        <!-- Modal de confirmación personalizado -->
        <div id="modal-confirmar" class="modal-overlay" style="display: none;">
            <div class="modal-contenido">
                <h3 id="modal-titulo">Confirmar acción</h3>
                <p id="modal-mensaje">¿Estás seguro de vaciar el carrito?</p>
                <div class="modal-botones">
                    <button id="modal-cancelar" class="btn-modal btn-cancelar">Cancelar</button>
                    <button id="modal-confirmar-btn" class="btn-modal btn-confirmar">Sí, vaciar</button>
                </div>
            </div>
        </div>

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

        <!-- Scripts API REST -->
        <script src="${pageContext.request.contextPath}/js/api.js"></script>
        <script src="${pageContext.request.contextPath}/js/carrito.js"></script>
    </body>
</html>
