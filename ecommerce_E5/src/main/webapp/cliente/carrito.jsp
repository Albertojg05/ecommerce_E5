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
        <title>Carrito</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index.jsp">MiTienda</a></div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
            </nav>
        </header>
        <main class="cart-container">
            <section class="cart-items-section">
                <h1>CARRITO</h1>
                <c:forEach var="item" items="${carrito}">
                    <div class="cart-item">
                        <img src="${pageContext.request.contextPath}/${not empty item.producto.imagenUrl ? item.producto.imagenUrl : 'imgs/default.png'}" width="80">
                        <div class="item-details">
                            <p class="item-title">${item.producto.nombre}</p>
                            <p>Precio: $<fmt:formatNumber value="${item.precioUnitario}" pattern="#,##0.00"/></p>
                            <div class="item-quantity">
                                <form action="${pageContext.request.contextPath}/cliente/carrito" method="post" style="display:inline">
                                    <input type="hidden" name="accion" value="actualizar">
                                    <input type="hidden" name="productoId" value="${item.producto.id}">
                                    <input type="hidden" name="cantidad" value="${item.cantidad - 1}">
                                    <button type="submit">-</button>
                                </form>
                                <span>${item.cantidad}</span>
                                <form action="${pageContext.request.contextPath}/cliente/carrito" method="post" style="display:inline">
                                    <input type="hidden" name="accion" value="actualizar">
                                    <input type="hidden" name="productoId" value="${item.producto.id}">
                                    <input type="hidden" name="cantidad" value="${item.cantidad + 1}">
                                    <button type="submit">+</button>
                                </form>
                            </div>
                            <form action="${pageContext.request.contextPath}/cliente/carrito" method="post">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="productoId" value="${item.producto.id}">
                                <button class="remove-btn">Quitar</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </section>
            <aside class="cart-summary-section">
                <div class="summary-box">
                    <h2>Total: $<fmt:formatNumber value="${total}" pattern="#,##0.00"/></h2>
                    <c:if test="${not empty carrito}">
                        <a href="${pageContext.request.contextPath}/cliente/checkout" class="checkout-btn">PAGAR</a>
                    </c:if>
                </div>
            </aside>
        </main>
    </body>
</html>
