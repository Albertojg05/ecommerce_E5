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
        <title>${producto.nombre} - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/product-detail.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index">MiTienda</a></div>
            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>
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
        <main class="product-detail-page">
            <!-- Mensaje de éxito -->
            <c:if test="${param.mensaje == 'resenaAgregada'}">
                <div class="alert alert-success">¡Reseña agregada exitosamente!</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <div class="product-detail-container">
                <div class="product-main-image">
                    <img src="${pageContext.request.contextPath}/${not empty producto.imagenUrl ? producto.imagenUrl : 'imgs/default.png'}" alt="${producto.nombre}">
                </div>
                <section class="product-info">
                    <h1 class="product-title">${producto.nombre}</h1>
                    <p class="product-price">$<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/></p>

                    <!-- Calificacion promedio -->
                    <div class="product-rating">
                        <c:forEach begin="1" end="5" var="i">
                            <span class="star ${i <= promedioCalificacion ? 'filled' : ''}">★</span>
                        </c:forEach>
                        <span class="rating-text">
                            (<fmt:formatNumber value="${promedioCalificacion}" pattern="#.#"/> de 5 - ${totalResenas} reseña<c:if test="${totalResenas != 1}">s</c:if>)
                        </span>
                    </div>

                    <p class="product-description">${producto.descripcion}</p>

                    <p class="product-stock">
                        <c:choose>
                            <c:when test="${producto.existencias > 0}">
                                <strong>En stock: ${producto.existencias} disponibles</strong>
                            </c:when>
                            <c:otherwise>
                                <strong>Agotado</strong>
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <c:if test="${producto.existencias > 0}">
                        <div class="add-to-cart-section">
                            <div class="quantity-selector">
                                <label>Cantidad:</label>
                                <input type="number" id="cantidad-producto" value="1" min="1" max="${producto.existencias}">
                            </div>
                            <button class="add-to-cart-btn" id="btn-agregar-carrito" data-id="${producto.id}">
                                AGREGAR AL CARRITO
                            </button>
                        </div>
                    </c:if>
                </section>
            </div>

            <!-- Sección de Reseñas -->
            <section class="reviews-section">
                <h2>Reseñas de Clientes</h2>

                <!-- Formulario para agregar reseña (solo usuarios logueados) -->
                <c:if test="${not empty sessionScope.usuarioLogueado}">
                    <div class="add-review-form">
                        <h3>Escribe tu reseña</h3>
                        <form id="form-resena" action="${pageContext.request.contextPath}/cliente/productos" method="post">
                            <input type="hidden" name="accion" value="agregarResena">
                            <input type="hidden" name="productoId" value="${producto.id}">

                            <div class="form-group">
                                <label>Calificacion:</label>
                                <div class="star-rating">
                                    <input type="radio" id="star5" name="calificacion" value="5" required>
                                    <label for="star5">★</label>
                                    <input type="radio" id="star4" name="calificacion" value="4">
                                    <label for="star4">★</label>
                                    <input type="radio" id="star3" name="calificacion" value="3">
                                    <label for="star3">★</label>
                                    <input type="radio" id="star2" name="calificacion" value="2">
                                    <label for="star2">★</label>
                                    <input type="radio" id="star1" name="calificacion" value="1">
                                    <label for="star1">★</label>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="comentario">Comentario:</label>
                                <textarea id="comentario" name="comentario" rows="4"
                                          placeholder="Escribe tu opinión sobre este producto..."
                                          minlength="10" required></textarea>
                            </div>

                            <button type="submit" class="btn-submit-review" id="btn-enviar-resena">Enviar Reseña</button>
                        </form>
                    </div>
                </c:if>
                <c:if test="${empty sessionScope.usuarioLogueado}">
                    <div class="login-to-review">
                        <p><a href="${pageContext.request.contextPath}/login?redirect=cliente/productos?accion=detalle%26id=${producto.id}">Inicia sesión</a> para escribir una reseña.</p>
                    </div>
                </c:if>

                <!-- Lista de reseñas existentes -->
                <div class="reviews-list">
                    <c:choose>
                        <c:when test="${not empty resenas}">
                            <c:forEach var="resena" items="${resenas}">
                                <div class="review-item">
                                    <div class="review-header">
                                        <span class="reviewer-name">${resena.usuario.nombre}</span>
                                        <span class="review-date">
                                            <fmt:formatDate value="${resena.fecha}" pattern="dd/MM/yyyy"/>
                                        </span>
                                    </div>
                                    <div class="review-rating">
                                        <c:forEach begin="1" end="5" var="i">
                                            <span class="star ${i <= resena.calificacion ? 'filled' : ''}">★</span>
                                        </c:forEach>
                                    </div>
                                    <p class="review-comment">${resena.comentario}</p>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="no-reviews">Este producto aún no tiene reseñas. ¡Sé el primero en opinar!</p>
                        </c:otherwise>
                    </c:choose>
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

            // Proteccion contra doble envio de resena
            const formResena = document.getElementById('form-resena');
            const btnEnviarResena = document.getElementById('btn-enviar-resena');
            let resenaEnviada = false;

            if (formResena && btnEnviarResena) {
                formResena.addEventListener('submit', function(e) {
                    if (resenaEnviada) {
                        e.preventDefault();
                        return false;
                    }
                    resenaEnviada = true;
                    btnEnviarResena.disabled = true;
                    btnEnviarResena.textContent = 'Enviando...';
                    btnEnviarResena.style.opacity = '0.7';
                    btnEnviarResena.style.cursor = 'not-allowed';
                });
            }
        </script>

        <!-- Scripts API REST -->
        <script src="${pageContext.request.contextPath}/js/api.js"></script>
        <script src="${pageContext.request.contextPath}/js/productos.js"></script>
    </body>
</html>
