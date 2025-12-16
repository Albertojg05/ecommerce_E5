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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Catálogo - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/products.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index">MiTienda</a></div>
            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>
            <form id="search-form" class="search-bar" action="${pageContext.request.contextPath}/cliente/productos" method="get">
                <input type="hidden" name="accion" value="buscar">
                <input type="text" id="search-input" name="q" placeholder="Buscar productos..." value="${param.q}">
                <button type="submit">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z" />
                    </svg>
                </button>
            </form>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.usuarioLogueado}">
                        <a href="${pageContext.request.contextPath}/cliente/cuenta">Mi Cuenta</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">Ingresar</a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>
        <main>
            <section class="category-header">
                <h1>Categorías</h1>
            </section>
            <section class="product-grid-container">
                <div class="filter-buttons">
                    <a href="${pageContext.request.contextPath}/cliente/productos" class="filter-btn ${empty param.id ? 'active' : ''}">Todos</a>
                    <c:forEach var="cat" items="${categorias}">
                        <a href="${pageContext.request.contextPath}/cliente/productos?accion=categoria&id=${cat.id}" class="filter-btn ${param.id == cat.id ? 'active' : ''}">${cat.nombre}</a>
                    </c:forEach>
                </div>
                <div class="product-grid">
                    <c:forEach var="p" items="${productos}">
                        <div class="product-card" data-id="${p.id}">
                            <a href="${pageContext.request.contextPath}/cliente/productos?accion=detalle&id=${p.id}" style="text-decoration:none; color:inherit;">
                                <img src="${(not empty p.imagenUrl and p.imagenUrl.startsWith('http')) ? p.imagenUrl : pageContext.request.contextPath.concat('/').concat(not empty p.imagenUrl ? p.imagenUrl : 'imgs/default.png')}"
                                     alt="${p.nombre}"
                                     onerror="this.src='${pageContext.request.contextPath}/imgs/default.png'"
                                     width="100%">
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

        <footer>
            <div class="footer-content">
                <div class="footer-section">
                    <h3>MiTienda</h3>
                    <p>Tu tienda de moda en línea con los mejores productos y precios.</p>
                </div>
                <div class="footer-section">
                    <h3>Enlaces</h3>
                    <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                    <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
                    <a href="${pageContext.request.contextPath}/cliente/cuenta">Mi Cuenta</a>
                </div>
                <div class="footer-section">
                    <h3>Contacto</h3>
                    <p>Email: contacto@mitienda.com</p>
                    <p>Tel: (123) 456-7890</p>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2025 MiTienda. Todos los derechos reservados.</p>
            </div>
        </footer>

        <script>
            // Mobile Menu Toggle
            const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
            const mainNav = document.querySelector('.main-nav');
            const overlay = document.querySelector('.mobile-nav-overlay');

            if (mobileMenuBtn) {
                mobileMenuBtn.addEventListener('click', function() {
                    this.classList.toggle('active');
                    mainNav.classList.toggle('active');
                    overlay.classList.toggle('active');
                    document.body.style.overflow = mainNav.classList.contains('active') ? 'hidden' : '';
                });

                overlay.addEventListener('click', function() {
                    mobileMenuBtn.classList.remove('active');
                    mainNav.classList.remove('active');
                    this.classList.remove('active');
                    document.body.style.overflow = '';
                });
            }
        </script>

    </body>
</html>