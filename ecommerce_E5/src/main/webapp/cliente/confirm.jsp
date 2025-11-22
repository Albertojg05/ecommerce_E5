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
        <title>Confirmación</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/confirm.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index.jsp">MiTienda</a></div>
        </header>
        <main class="confirmation-page">
            <div class="confirmation-box">
                <h1>¡Gracias por tu compra!</h1>
                <p class="order-number">Pedido <strong>${pedido.numeroPedido}</strong> confirmado.</p>
                <p>Total: $<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></p>
                <a href="${pageContext.request.contextPath}/cliente/productos" class="btn-back-home">Seguir Comprando</a>
            </div>
        </main>
    </body>
</html>
