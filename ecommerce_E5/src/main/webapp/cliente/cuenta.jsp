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
        <title>Mi Cuenta - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/account.css">
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
                <a href="${pageContext.request.contextPath}/logout">Cerrar Sesión</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>
        <main class="account-page">
            <c:if test="${param.mensaje == 'actualizado'}">
                <div class="alert alert-success">Datos actualizados correctamente.</div>
            </c:if>
            <c:if test="${param.mensaje == 'cancelado'}">
                <div class="alert alert-success">Pedido cancelado correctamente.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <div class="account-container">
                <aside class="account-sidebar">
                    <div class="user-info">
                        <div class="user-avatar">
                            <span>${usuario.nombre.substring(0,1).toUpperCase()}</span>
                        </div>
                        <h2>${usuario.nombre}</h2>
                        <p class="user-email">${usuario.correo}</p>
                    </div>
                    <nav class="account-nav">
                        <a href="${pageContext.request.contextPath}/cliente/cuenta" class="active">Mis Pedidos</a>
                        <a href="${pageContext.request.contextPath}/cliente/cuenta?accion=editar">Editar Perfil</a>
                        <a href="${pageContext.request.contextPath}/logout" class="logout-link">Cerrar Sesión</a>
                    </nav>
                </aside>

                <section class="account-content">
                    <h1>Mis Pedidos</h1>

                    <c:choose>
                        <c:when test="${not empty historial}">
                            <div class="orders-list">
                                <c:forEach var="ped" items="${historial}">
                                    <div class="order-card">
                                        <div class="order-header">
                                            <div class="order-info">
                                                <span class="order-number">Pedido #${ped.numeroPedido}</span>
                                                <span class="order-date">
                                                    <fmt:formatDate value="${ped.fecha}" pattern="dd/MM/yyyy"/>
                                                </span>
                                            </div>
                                            <span class="order-status status-${ped.estado.toString().toLowerCase()}">${ped.estado}</span>
                                        </div>
                                        <div class="order-body">
                                            <div class="order-items-preview">
                                                <c:forEach var="detalle" items="${ped.detalles}" end="2">
                                                    <img src="${(not empty detalle.producto.imagenUrl and detalle.producto.imagenUrl.startsWith('http')) ? detalle.producto.imagenUrl : pageContext.request.contextPath.concat('/').concat(not empty detalle.producto.imagenUrl ? detalle.producto.imagenUrl : 'imgs/default.png')}"
                                                         alt="${detalle.producto.nombre}" class="item-thumbnail">
                                                </c:forEach>
                                                <c:if test="${ped.detalles.size() > 3}">
                                                    <span class="more-items">+${ped.detalles.size() - 3}</span>
                                                </c:if>
                                            </div>
                                            <div class="order-total">
                                                <span class="total-label">Total</span>
                                                <span class="total-amount">$<fmt:formatNumber value="${ped.total}" pattern="#,##0.00"/></span>
                                            </div>
                                        </div>
                                        <div class="order-actions">
                                            <button type="button" class="btn-ver-pedido-js" data-id="${ped.id}">Ver Detalles</button>
                                            <c:if test="${ped.estado == 'PENDIENTE'}">
                                                <button type="button" class="btn-cancel-order btn-cancelar-pedido-js" data-id="${ped.id}">Cancelar Pedido</button>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-orders">
                                <p>No tienes pedidos aún.</p>
                                <a href="${pageContext.request.contextPath}/cliente/productos" class="btn-shop">Ir a comprar</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
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
        </script>

        <!-- Scripts API REST -->
        <script src="${pageContext.request.contextPath}/js/api.js"></script>
        <script src="${pageContext.request.contextPath}/js/pedidos.js"></script>
        <script src="${pageContext.request.contextPath}/js/perfil.js"></script>
    </body>
</html>