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
        <title>Gestionar Productos - Admin</title>
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
            <div class="admin-header">
                <h1>Gestionar Productos</h1>
                <a href="${pageContext.request.contextPath}/admin/productos?accion=nuevo" class="btn btn-primary">
                    + Agregar Nuevo Producto
                </a>
            </div>

            <c:if test="${param.success == 'created'}">
                <div class="success-message">Producto creado exitosamente.</div>
            </c:if>
            <c:if test="${param.success == 'updated'}">
                <div class="success-message">Producto actualizado exitosamente.</div>
            </c:if>
            <c:if test="${param.success == 'deleted'}">
                <div class="success-message">Producto eliminado exitosamente.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <table class="product-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Imagen</th>
                        <th>Nombre</th>
                        <th>Categoría</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty productos}">
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 20px;">
                                No hay productos registrados.
                            </td>
                        </tr>
                    </c:if>

                    <c:forEach var="producto" items="${productos}">
                        <tr>
                            <td>${producto.id}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty producto.imagenUrl}">
                                        <img src="${producto.imagenUrl}" 
                                             alt="${producto.nombre}" 
                                             style="width: 60px; height: 80px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://placehold.co/60x80/ccc/666?text=Sin+imagen" 
                                             alt="Sin imagen">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong>${producto.nombre}</strong><br>
                                <small>${producto.color} - ${producto.talla}</small>
                            </td>
                            <td>${producto.categoria.nombre}</td>
                            <td>$<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${producto.existencias < 10}">
                                        <span style="color: #e74c3c; font-weight: bold;">
                                            ${producto.existencias}
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        ${producto.existencias}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/productos?accion=editar&id=${producto.id}" 
                                   class="btn btn-edit">Editar</a>
                                <button onclick="confirmarEliminar(${producto.id}, '${producto.nombre}')" 
                                        class="btn btn-delete">Eliminar</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </main>

        <script>
            function confirmarEliminar(id, nombre) {
                if (confirm('¿Estás seguro de eliminar el producto "' + nombre + '"?')) {
                    window.location.href = '${pageContext.request.contextPath}/admin/productos?accion=eliminar&id=' + id;
                }
            }
        </script>
    </body>
</html>