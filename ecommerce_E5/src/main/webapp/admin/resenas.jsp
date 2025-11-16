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
        <title>Moderar Reseñas - Admin</title>
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
            <h1>Moderar Reseñas</h1>

            <c:if test="${param.success == 'deleted'}">
                <div class="success-message">Reseña eliminada exitosamente.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <div style="background: var(--background-light); padding: 15px; border-radius: 8px; margin-bottom: 30px;">
                <strong>Total de reseñas:</strong> ${resenas.size()}
            </div>

            <c:if test="${empty resenas}">
                <div style="text-align: center; padding: 40px; background: var(--background-light); border-radius: 8px;">
                    <p style="color: #666; font-size: 16px;">No hay reseñas registradas.</p>
                </div>
            </c:if>

            <c:forEach var="resena" items="${resenas}">
                <div class="resena-card">
                    <div class="resena-header">
                        <div>
                            <div class="resena-usuario">${resena.usuario.nombre}</div>
                            <div class="resena-fecha">
                                <fmt:formatDate value="${resena.fecha}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>
                        <div class="resena-calificacion">
                            <div class="stars">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= resena.calificacion}">
                                            <span class="star-filled">★</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="star-empty">★</span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <span style="margin-left: 10px; color: #666; font-size: 14px;">
                                    (${resena.calificacion}/5)
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="resena-producto">
                        <strong>Producto:</strong> ${resena.producto.nombre}
                    </div>

                    <div class="resena-comentario">
                        <strong>Comentario:</strong><br>
                        ${resena.comentario}
                    </div>

                    <div style="display: flex; gap: 10px;">
                        <form method="post" 
                              action="${pageContext.request.contextPath}/admin/resenas" 
                              onsubmit="return confirm('¿Estás seguro de eliminar esta reseña?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="${resena.id}">
                            <button type="submit" class="btn btn-delete">
                                Eliminar Reseña
                            </button>
                        </form>

                        <a href="${pageContext.request.contextPath}/admin/productos?accion=editar&id=${resena.producto.id}" 
                           class="btn btn-edit" 
                           style="text-decoration: none; display: inline-block;">
                            Ver Producto
                        </a>
                    </div>
                </div>
            </c:forEach>
        </main>
    </body>
</html>
