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
        <title>Gestionar Pedidos - Admin</title>
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
            <h1>Gestionar Pedidos</h1>

            <c:if test="${param.success == 'updated'}">
                <div class="success-message">Estado del pedido actualizado exitosamente.</div>
            </c:if>
            <c:if test="${param.success == 'cancelled'}">
                <div class="success-message">Pedido cancelado exitosamente.</div>
            </c:if>

            <div class="filter-section">
                <form method="get" action="${pageContext.request.contextPath}/admin/pedidos">
                    <input type="hidden" name="accion" value="filtrar">
                    <label for="estado">Filtrar por Estado:</label>
                    <select id="estado" name="estado" onchange="this.form.submit()">
                        <option value="TODOS" ${empty estadoSeleccionado ? 'selected' : ''}>Todos</option>
                        <c:forEach var="estado" items="${estados}">
                            <option value="${estado}" ${estadoSeleccionado == estado ? 'selected' : ''}>
                                ${estado}
                            </option>
                        </c:forEach>
                    </select>
                </form>
            </div>

            <table class="product-table">
                <thead>
                    <tr>
                        <th>Número Pedido</th>
                        <th>Cliente</th>
                        <th>Fecha</th>
                        <th>Total</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty pedidos}">
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 20px;">
                                No hay pedidos registrados.
                            </td>
                        </tr>
                    </c:if>

                    <c:forEach var="pedido" items="${pedidos}">
                        <tr>
                            <td><strong>${pedido.numeroPedido}</strong></td>
                            <td>${pedido.usuario.nombre}</td>
                            <td>
                                <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy"/>
                                <br>
                                <small><fmt:formatDate value="${pedido.fecha}" pattern="HH:mm"/></small>
                            </td>
                            <td>
                                <strong>$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></strong>
                            </td>
                            <td>
                                <span class="badge badge-${pedido.estado.name().toLowerCase()}">
                                    ${pedido.estado}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/pedidos?accion=detalle&id=${pedido.id}" 
                                   class="btn btn-edit">Ver Detalle</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </main>
    </body>
</html>