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
        <title>${producto.nombre} - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/product-detail.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index.jsp">MiTienda</a></div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
            </nav>
        </header>
        <main class="product-detail-page">
            <div class="product-detail-container">
                <div class="product-main-image">
                    <img src="${pageContext.request.contextPath}/${not empty producto.imagenUrl ? producto.imagenUrl : 'imgs/default.png'}" width="100%">
                </div>
                <section class="product-info">
                    <h1 class="product-title">${producto.nombre}</h1>
                    <p class="product-price">$<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/></p>
                    <p>${producto.descripcion}</p>

                    <form action="${pageContext.request.contextPath}/cliente/carrito" method="post">
                        <input type="hidden" name="accion" value="agregar">
                        <input type="hidden" name="productoId" value="${producto.id}">
                        <div style="margin: 20px 0;">
                            <label>Cantidad:</label>
                            <input type="number" name="cantidad" value="1" min="1" max="${producto.existencias}" style="width: 60px; padding: 5px;">
                        </div>
                        <button class="add-to-cart-btn" type="submit">AGREGAR AL CARRITO</button>
                    </form>
                </section>
            </div>
        </main>
    </body>
</html>
