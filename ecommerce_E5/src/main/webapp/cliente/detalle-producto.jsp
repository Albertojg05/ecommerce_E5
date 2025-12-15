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
                    <img src="${(not empty producto.imagenUrl and producto.imagenUrl.startsWith('http')) ? producto.imagenUrl : pageContext.request.contextPath.concat('/').concat(not empty producto.imagenUrl ? producto.imagenUrl : 'imgs/default.png')}" alt="${producto.nombre}">
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

                    <c:if test="${not empty producto.color}">
                        <p class="product-color"><strong>Color:</strong> ${producto.color}</p>
                    </c:if>

                    <!-- Selector de tallas -->
                    <c:if test="${not empty tallasProducto}">
                        <%-- Contar tallas disponibles (con stock > 0) --%>
                        <c:set var="tallasDisponibles" value="0"/>
                        <c:set var="tallaUnicaId" value=""/>
                        <c:set var="tallaUnicaStock" value="0"/>
                        <c:forEach var="t" items="${tallasProducto}">
                            <c:if test="${t.stock > 0}">
                                <c:set var="tallasDisponibles" value="${tallasDisponibles + 1}"/>
                                <c:set var="tallaUnicaId" value="${t.id}"/>
                                <c:set var="tallaUnicaStock" value="${t.stock}"/>
                            </c:if>
                        </c:forEach>

                        <div class="talla-selector">
                            <label><strong>Selecciona tu talla:</strong></label>
                            <div class="tallas-grid">
                                <c:forEach var="talla" items="${tallasProducto}">
                                    <button type="button"
                                            class="talla-btn ${talla.stock <= 0 ? 'agotada' : ''} ${tallasDisponibles == 1 && talla.stock > 0 ? 'selected' : ''}"
                                            data-talla-id="${talla.id}"
                                            data-stock="${talla.stock}"
                                            ${talla.stock <= 0 ? 'disabled' : ''}>
                                        ${talla.talla}
                                        <c:if test="${talla.stock <= 0}">
                                            <span class="agotado-text">Agotado</span>
                                        </c:if>
                                    </button>
                                </c:forEach>
                            </div>
                            <input type="hidden" id="talla-seleccionada" value="${tallasDisponibles == 1 ? tallaUnicaId : ''}">
                        </div>

                        <p class="product-stock" id="stock-info" style="${tallasDisponibles == 1 ? '' : 'display: none;'}">
                            <strong>Stock disponible: <span id="stock-cantidad">${tallasDisponibles == 1 ? tallaUnicaStock : '0'}</span></strong>
                        </p>

                        <div class="add-to-cart-section">
                            <div class="quantity-selector">
                                <label>Cantidad:</label>
                                <input type="number" id="cantidad-producto" value="1" min="1"
                                       max="${tallasDisponibles == 1 ? (tallaUnicaStock > 10 ? 10 : tallaUnicaStock) : 1}"
                                       ${tallasDisponibles == 1 ? '' : 'disabled'}>
                            </div>
                            <button class="add-to-cart-btn" id="btn-agregar-carrito" data-id="${producto.id}"
                                    ${tallasDisponibles == 1 ? '' : 'disabled'}>
                                ${tallasDisponibles == 1 ? 'AGREGAR AL CARRITO' : 'SELECCIONA UNA TALLA'}
                            </button>
                        </div>
                    </c:if>

                    <c:if test="${empty tallasProducto}">
                        <p class="product-stock"><strong>Producto sin tallas disponibles</strong></p>
                    </c:if>
                </section>
            </div>

            <!-- Sección de Reseñas -->
            <section class="reviews-section">
                <h2>Reseñas de Clientes</h2>

                <!-- Formulario para agregar reseña (solo usuarios que compraron el producto) -->
                <c:choose>
                    <c:when test="${empty sessionScope.usuarioLogueado}">
                        <div class="login-to-review">
                            <p><a href="${pageContext.request.contextPath}/login?redirect=cliente/productos?accion=detalle%26id=${producto.id}">Inicia sesión</a> para escribir una reseña.</p>
                        </div>
                    </c:when>
                    <c:when test="${puedeResenar}">
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
                    </c:when>
                    <c:otherwise>
                        <div class="login-to-review">
                            <p>Debes comprar este producto para poder escribir una reseña.</p>
                        </div>
                    </c:otherwise>
                </c:choose>

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

        <!-- Modal de error/info -->
        <div id="modal-mensaje" class="modal-overlay" style="display: none;">
            <div class="modal-contenido">
                <h3 id="modal-titulo">Aviso</h3>
                <p id="modal-texto"></p>
                <div class="modal-botones">
                    <button id="modal-cerrar" class="btn-modal btn-confirmar">Aceptar</button>
                </div>
            </div>
        </div>

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

            // Selector de tallas
            const tallaBtns = document.querySelectorAll('.talla-btn:not(.agotada)');
            const tallaInput = document.getElementById('talla-seleccionada');
            const stockInfo = document.getElementById('stock-info');
            const stockCantidad = document.getElementById('stock-cantidad');
            const cantidadInput = document.getElementById('cantidad-producto');
            const btnAgregarCarrito = document.getElementById('btn-agregar-carrito');

            tallaBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    // Quitar seleccion anterior
                    document.querySelectorAll('.talla-btn').forEach(b => b.classList.remove('selected'));

                    // Seleccionar esta talla
                    this.classList.add('selected');
                    const tallaId = this.dataset.tallaId;
                    const stock = parseInt(this.dataset.stock);

                    // Actualizar input oculto
                    tallaInput.value = tallaId;

                    // Mostrar stock disponible
                    stockInfo.style.display = 'block';
                    stockCantidad.textContent = stock;

                    // Habilitar y configurar selector de cantidad
                    cantidadInput.disabled = false;
                    cantidadInput.max = Math.min(stock, 10);
                    cantidadInput.value = 1;

                    // Habilitar boton de agregar al carrito
                    btnAgregarCarrito.disabled = false;
                    btnAgregarCarrito.textContent = 'AGREGAR AL CARRITO';
                });
            });

            // Validar cantidad al cambiar y al escribir
            if (cantidadInput) {
                // Validar en tiempo real mientras escribe
                cantidadInput.addEventListener('input', function() {
                    const max = parseInt(this.max) || 10;
                    const min = parseInt(this.min) || 1;
                    let val = parseInt(this.value);

                    if (!isNaN(val)) {
                        if (val > max) {
                            this.value = max;
                        } else if (val < min) {
                            this.value = min;
                        }
                    }
                });

                // Validar al perder foco (para valores vacíos)
                cantidadInput.addEventListener('blur', function() {
                    const max = parseInt(this.max) || 10;
                    const min = parseInt(this.min) || 1;
                    let val = parseInt(this.value);

                    if (isNaN(val) || val < min) {
                        this.value = min;
                    } else if (val > max) {
                        this.value = max;
                    }
                });
            }

            // Modal de mensajes
            const modalMensaje = document.getElementById('modal-mensaje');
            const modalTitulo = document.getElementById('modal-titulo');
            const modalTexto = document.getElementById('modal-texto');
            const modalCerrar = document.getElementById('modal-cerrar');

            function mostrarModal(titulo, mensaje) {
                modalTitulo.textContent = titulo;
                modalTexto.textContent = mensaje;
                modalMensaje.style.display = 'flex';
            }

            if (modalCerrar) {
                modalCerrar.addEventListener('click', function() {
                    modalMensaje.style.display = 'none';
                });
            }

            // Cerrar modal al hacer clic fuera
            if (modalMensaje) {
                modalMensaje.addEventListener('click', function(e) {
                    if (e.target === this) {
                        this.style.display = 'none';
                    }
                });
            }

            // Agregar al carrito via API REST
            if (btnAgregarCarrito) {
                btnAgregarCarrito.addEventListener('click', async function() {
                    const productoId = parseInt(this.dataset.id);
                    const tallaId = parseInt(tallaInput.value);

                    if (!tallaId) {
                        mostrarModal('Selecciona una talla', 'Por favor selecciona una talla antes de agregar al carrito.');
                        return;
                    }

                    // Validar y ajustar cantidad antes de enviar
                    const maxCantidad = parseInt(cantidadInput.max) || 10;
                    let cantidad = parseInt(cantidadInput.value) || 1;

                    // Forzar limites
                    if (cantidad < 1) cantidad = 1;
                    if (cantidad > maxCantidad) cantidad = maxCantidad;
                    cantidadInput.value = cantidad;

                    // Deshabilitar boton mientras procesa
                    this.disabled = true;
                    const textoOriginal = this.textContent;
                    this.textContent = 'AGREGANDO...';

                    try {
                        const response = await fetch('${pageContext.request.contextPath}/api/carrito', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                productoId: productoId,
                                tallaId: tallaId,
                                cantidad: cantidad
                            })
                        });

                        const data = await response.json();

                        if (data.success) {
                            this.textContent = 'AGREGADO';
                            setTimeout(() => {
                                this.textContent = textoOriginal;
                                this.disabled = false;
                            }, 1500);
                        } else {
                            const mensaje = data.message || 'Error al agregar al carrito';
                            const titulo = mensaje.toLowerCase().includes('stock') ? 'Stock Insuficiente' : 'Error';
                            mostrarModal(titulo, mensaje);
                            this.textContent = textoOriginal;
                            this.disabled = false;
                        }
                    } catch (error) {
                        console.error('Error:', error);
                        mostrarModal('Error de conexión', 'No se pudo conectar con el servidor. Intenta de nuevo.');
                        this.textContent = textoOriginal;
                        this.disabled = false;
                    }
                });
            }
        </script>

        <!-- Scripts API REST -->
        <script src="${pageContext.request.contextPath}/js/api.js"></script>
        <script src="${pageContext.request.contextPath}/js/productos.js"></script>
    </body>
</html>
