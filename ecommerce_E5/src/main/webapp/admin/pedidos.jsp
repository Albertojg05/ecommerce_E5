<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Pedidos - Admin</title>
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
        <h1>Gestión de Pedidos</h1>

        <!-- Filtro por estado -->
        <div class="filter-section">
            <form method="get" action="${pageContext.request.contextPath}/admin/pedidos">
                <input type="hidden" name="accion" value="filtrar">
                <label for="estado">Filtrar por estado:</label>
                <select id="estado" name="estado" onchange="this.form.submit()">
                    <option value="TODOS">Todos</option>
                    <c:forEach var="estado" items="${estados}">
                        <c:set var="estadoLower" value="${fn:toLowerCase(estado)}" />
                        <c:set var="estadoCapitalized" value="${fn:toUpperCase(fn:substring(estadoLower, 0, 1))}${fn:substring(estadoLower, 1, -1)}" />
                        <option value="${estado}"
                                ${estadoSeleccionado == estado.name() ? 'selected' : ''}>
                            ${estadoCapitalized}
                        </option>
                    </c:forEach>
                </select>
            </form>
        </div>

        <c:if test="${param.success == 'updated'}">
            <div class="success-message">Pedido actualizado exitosamente</div>
        </c:if>

        <c:if test="${param.success == 'cancelled'}">
            <div class="success-message">Pedido cancelado exitosamente</div>
        </c:if>

        <table class="product-table">
            <thead>
                <tr>
                    <th>Número</th>
                    <th>Cliente</th>
                    <th>Fecha</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Pago</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="pedido" items="${pedidos}">
                    <tr>
                        <td data-label="Número"><strong>${pedido.numeroPedido}</strong></td>
                        <td data-label="Cliente">${pedido.usuario.nombre}</td>
                        <td data-label="Fecha">
                            <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy HH:mm"/>
                        </td>
                        <td data-label="Total">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></td>
                        <td data-label="Estado">
                            <c:set var="estadoPedidoLower" value="${fn:toLowerCase(pedido.estado)}" />
                            <span class="badge badge-${pedido.estado.name().toLowerCase()}">
                                ${fn:toUpperCase(fn:substring(estadoPedidoLower, 0, 1))}${fn:substring(estadoPedidoLower, 1, -1)}
                            </span>
                        </td>
                        <td data-label="Pago">
                            <c:set var="estadoPagoLower" value="${fn:toLowerCase(pedido.pago.estado)}" />
                            <span class="badge badge-${pedido.pago.estado.name().toLowerCase()}">
                                ${fn:toUpperCase(fn:substring(estadoPagoLower, 0, 1))}${fn:substring(estadoPagoLower, 1, -1)}
                            </span>
                        </td>
                        <td data-label="Acciones">
                            <a href="${pageContext.request.contextPath}/admin/pedidos?accion=detalle&id=${pedido.id}"
                               class="btn btn-edit">Ver Detalle</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
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