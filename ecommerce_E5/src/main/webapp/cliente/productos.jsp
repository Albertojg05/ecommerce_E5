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
        <title>Catálogo - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/product.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index.jsp">MiTienda</a></div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                <a href="${pageContext.request.contextPath}/cliente/cuenta">Cuenta</a>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
            </nav>
        </header>
        <main>
            <section class="category-header">
                <h1>CATÁLOGO</h1>
            </section>
            <section class="product-grid-container">
                <div class="filter-buttons">
                    <a href="${pageContext.request.contextPath}/cliente/productos" style="margin-right:10px;">Todos</a>
                    <c:forEach var="cat" items="${categorias}">
                        <a href="${pageContext.request.contextPath}/cliente/productos?accion=categoria&id=${cat.id}" style="margin-right:10px;">${cat.nombre}</a>
                    </c:forEach>
                </div>
                <div class="product-grid">
                    <c:forEach var="p" items="${productos}">
                        <div class="product-card">
                            <a href="${pageContext.request.contextPath}/cliente/productos?accion=detalle&id=${p.id}" style="text-decoration:none; color:inherit;">
                                <img src="${pageContext.request.contextPath}/${not empty p.imagenUrl ? p.imagenUrl : 'imgs/default.png'}" width="100%">
                                <div class="product-info">
                                    <p class="product-title">${p.nombre}</p>
                                    <p class="product-price">$<fmt:formatNumber value="${p.precio}" pattern="#,##0.00"/></p>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </main>
    </body>
</html>