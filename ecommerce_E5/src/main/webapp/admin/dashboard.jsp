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
        <title>Dashboard - Admin MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/admin/dashboard">MiTienda Admin</a></div>
            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/producto">Productos</a>
                <a href="${pageContext.request.contextPath}/admin/pedidos">Pedidos</a>
                <a href="${pageContext.request.contextPath}/admin/resenas">Reseñas</a>
                <a href="${pageContext.request.contextPath}/admin/logout">Salir</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>

        <main class="admin-container">
            <div class="admin-header">
                <h1>Dashboard</h1>
                <p><strong>Bienvenido, ${sessionScope.usuarioNombre}.</strong></p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <div class="dashboard-stats">
                <div class="stat-card">
                    <h3>Total Productos</h3>
                    <div class="number">${totalProductos}</div>
                </div>
                <div class="stat-card">
                    <h3>Total Pedidos</h3>
                    <div class="number">${totalPedidos}</div>
                </div>
                <div class="stat-card">
                    <h3>Total Reseñas</h3>
                    <div class="number">${totalResenas}</div>
                </div>
                <div class="stat-card">
                    <h3>Productos Stock Bajo</h3>
                    <div class="number">${productosStockBajo}</div>
                </div>
            </div>

            <div class="recent-orders">
                <h2>Pedidos Recientes</h2>
                <c:choose>
                    <c:when test="${empty pedidosRecientes}">
                        <!-- #38: Mensaje cuando no hay pedidos recientes -->
                        <div class="empty-state" style="text-align: center; padding: 40px 20px; background: #f9f9f9; border-radius: 8px; color: #666;">
                            <p style="margin: 0 0 15px 0; font-size: 16px;">No hay pedidos recientes</p>
                            <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn btn-edit" style="display: inline-block;">Ver todos los pedidos</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="pedido" items="${pedidosRecientes}">
                            <div class="order-item">
                                <div>
                                    <strong>Pedido ${pedido.numeroPedido}</strong>
                                    <br>
                                    <small>${pedido.usuario.nombre}</small>
                                </div>
                                <div>
                                    <span class="badge badge-${pedido.estado.name().toLowerCase()}">${pedido.estado}</span>
                                </div>
                                <div>
                                    <strong>$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></strong>
                                </div>
                                <div>
                                    <a href="${pageContext.request.contextPath}/admin/pedidos?accion=detalle&id=${pedido.id}"
                                       class="btn btn-edit">Ver</a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
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