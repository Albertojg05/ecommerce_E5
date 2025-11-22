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
        <title>Mi Cuenta</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/acount.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index.jsp">MiTienda</a></div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                <a href="${pageContext.request.contextPath}/logout">Cerrar Sesión</a>
            </nav>
        </header>
        <main class="account-page">
            <h1>Hola, ${usuario.nombre}</h1>
            <h2>Mis Pedidos</h2>
            <div class="order-list">
                <c:forEach var="ped" items="${historial}">
                    <div class="order-item">
                        <p><strong>${ped.numeroPedido}</strong> - <fmt:formatDate value="${ped.fecha}" pattern="dd/MM/yyyy"/></p>
                        <p>Estado: ${ped.estado}</p>
                        <p class="order-price">$<fmt:formatNumber value="${ped.total}" pattern="#,##0.00"/></p>
                    </div>
                </c:forEach>
                <c:if test="${empty historial}">
                    <p>No tienes pedidos aún.</p>
                </c:if>
            </div>
        </main>
    </body>
</html>