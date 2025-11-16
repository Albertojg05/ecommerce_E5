<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard - Administración</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-common.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo">MiTienda Admin</div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/admin/pedidos">Pedidos</a>
                <a href="${pageContext.request.contextPath}/admin/resenas">Reseñas</a>
                <a href="${pageContext.request.contextPath}/admin/logout">Salir</a>
            </nav>
        </header>

        <main class="admin-container">
            <h1>Dashboard - Panel de Administración</h1>

            <p style="margin-bottom: 30px;">
                Bienvenido, <strong>${usuarioLogueado.nombre}</strong>
            </p>

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
                    <h3>Stock Bajo</h3>
                    <div class="number" style="color: #e74c3c;">${productosStockBajo}</div>
                </div>
            </div>

            <div class="recent-orders">
                <h2>Pedidos Recientes</h2>

                <c:if test="${empty pedidosRecientes}">
                    <p style="color: #666;">No hay pedidos recientes.</p>
                </c:if>

                <c:forEach var="pedido" items="${pedidosRecientes}">
                    <div class="order-item">
                        <div>
                            <strong>${pedido.numeroPedido}</strong><br>
                            <small>Cliente: ${pedido.usuario.nombre}</small><br>
                            <small><fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy HH:mm"/></small>
                        </div>
                        <div>
                            <span class="badge badge-${pedido.estado.name().toLowerCase()}">
                                ${pedido.estado}
                            </span>
                            <strong style="margin-left: 15px;">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></strong>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div style="margin-top: 40px; display: flex; gap: 15px;">
                <a href="${pageContext.request.contextPath}/admin/productos?accion=nuevo" class="btn btn-primary">
                    + Nuevo Producto
                </a>
                <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn btn-primary">
                    Ver Todos los Pedidos
                </a>
            </div>
        </main>
    </body>
</html>