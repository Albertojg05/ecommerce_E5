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
        <title>Checkout</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkout.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index.jsp">MiTienda</a></div>
        </header>
        <main class="checkout-page">
            <h1>Finalizar Compra</h1>
            <div class="checkout-container">
                <form class="shipping-form" action="${pageContext.request.contextPath}/cliente/checkout" method="post">
                    <h2>Envío</h2>
                    <input type="text" name="calle" placeholder="Calle" required class="form-input">
                    <input type="text" name="ciudad" placeholder="Ciudad" required class="form-input">
                    <input type="text" name="estado" placeholder="Estado" required class="form-input">
                    <input type="text" name="codigoPostal" placeholder="CP" required class="form-input">

                    <h2>Pago</h2>
                    <c:forEach var="m" items="${metodosPago}">
                        <div><input type="radio" name="metodoPago" value="${m}" checked> ${m}</div>
                    </c:forEach>

                    <button type="submit" class="checkout-btn" style="margin-top:20px;">CONFIRMAR COMPRA</button>
                </form>
                <aside class="order-summary">
                    <h2>Total a Pagar: $<fmt:formatNumber value="${total}" pattern="#,##0.00"/></h2>
                </aside>
            </div>
        </main>
    </body>
</html>