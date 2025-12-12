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
        <title>MiTienda - Inicio</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/home.css">
    </head>
    <body>

        <header>
            <div class="logo">
                <a href="${pageContext.request.contextPath}/index">MiTienda</a>
            </div>

            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>

            <form class="search-bar" action="${pageContext.request.contextPath}/cliente/productos" method="get">
                <input type="hidden" name="accion" value="buscar">
                <input type="text" name="q" placeholder="Buscar productos...">
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
            <section class="hero-section">
            </section>

            <section class="featured-categories">
                <h2>Destacados</h2>
                <div class="categories-container">
                    <c:forEach var="cat" items="${categorias}">
                        <div class="category-item">
                            <a href="${pageContext.request.contextPath}/cliente/productos?accion=categoria&id=${cat.id}">
                                <c:choose>
                                    <c:when test="${cat.nombre == 'Vestidos'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/vestidos.png" alt="Vestidos">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Tops'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/tops.png" alt="Tops">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Blusas'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/blusas.png" alt="Blusas">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Blazers'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/blazers.png" alt="Blazers">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Pantalones'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/pantalones.png" alt="Pantalones">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Conjuntos'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/conjuntoRojo.png" alt="Conjuntos">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Accesorios'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/accesorios.png" alt="Accesorios">
                                    </c:when>
                                    <c:when test="${cat.nombre == 'Faldas'}">
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/faldas.png" alt="Faldas">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/imgs/categoria/tops.png" alt="${cat.nombre}">
                                    </c:otherwise>
                                </c:choose>
                                <span>${cat.nombre}</span>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </section>

            <section class="product-section">
                <h2>Categorías</h2>

                <div class="filter-buttons">
                    <button class="active" onclick="window.location.href='${pageContext.request.contextPath}/cliente/productos'">Todos</button>
                    <c:forEach var="cat" items="${categorias}">
                        <button onclick="window.location.href='${pageContext.request.contextPath}/cliente/productos?accion=categoria&id=${cat.id}'">${cat.nombre}</button>
                    </c:forEach>
                </div>

                <div class="product-list-wrapper">
                    <button class="scroll-arrow left" id="scrollLeft">‹</button>
                    <button class="scroll-arrow right" id="scrollRight">›</button>
                    <div class="product-list" id="productList">
                        <c:if test="${empty productosDestacados}">
                            <p>No hay productos disponibles.</p>
                        </c:if>

                        <c:forEach var="p" items="${productosDestacados}">
                            <div class="product-item">
                                <a href="${pageContext.request.contextPath}/cliente/productos?accion=detalle&id=${p.id}" style="text-decoration:none; color:inherit;">
                                    <img src="${pageContext.request.contextPath}/${not empty p.imagenUrl ? p.imagenUrl : 'imgs/default.png'}"
                                         alt="${p.nombre}"
                                         onerror="this.src='${pageContext.request.contextPath}/imgs/default.png'">
                                    <p class="product-title">${p.nombre}</p>
                                    <p class="product-price">
                                        <span class="sale-price">$<fmt:formatNumber value="${p.precio}" pattern="#,##0.00"/></span>
                                    </p>
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </section>

            <script>
                (function() {
                    const productList = document.getElementById('productList');
                    const scrollLeftBtn = document.getElementById('scrollLeft');
                    const scrollRightBtn = document.getElementById('scrollRight');
                    const scrollAmount = 280;

                    // Ocultar botón izquierdo inicialmente
                    scrollLeftBtn.style.display = 'none';

                    function updateArrowVisibility() {
                        const maxScroll = productList.scrollWidth - productList.clientWidth;
                        const canScrollLeft = productList.scrollLeft > 0;
                        const canScrollRight = productList.scrollLeft < maxScroll - 5;

                        scrollLeftBtn.style.display = canScrollLeft ? 'flex' : 'none';
                        scrollRightBtn.style.display = canScrollRight && maxScroll > 10 ? 'flex' : 'none';
                    }

                    scrollLeftBtn.addEventListener('click', function() {
                        productList.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
                    });

                    scrollRightBtn.addEventListener('click', function() {
                        productList.scrollBy({ left: scrollAmount, behavior: 'smooth' });
                    });

                    productList.addEventListener('scroll', updateArrowVisibility);
                    window.addEventListener('resize', updateArrowVisibility);
                    window.addEventListener('load', updateArrowVisibility);

                    // Actualizar después de un pequeño delay para asegurar cálculo correcto
                    setTimeout(updateArrowVisibility, 100);
                })();
            </script>
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