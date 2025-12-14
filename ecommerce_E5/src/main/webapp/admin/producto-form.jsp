<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty producto ? 'Nuevo' : 'Editar'} Producto - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <header class="header-productos">
        <div class="logo"><a href="${pageContext.request.contextPath}/admin/dashboard">MiTienda Admin</a></div>
        <button class="mobile-menu-btn" aria-label="Menu">
            <span></span>
            <span></span>
            <span></span>
        </button>
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/producto">Productos</a>
            <a href="${pageContext.request.contextPath}/admin/pedidos">Pedidos</a>
            <a href="${pageContext.request.contextPath}/admin/resenas">Reseñas</a>
            <a href="${pageContext.request.contextPath}/admin/logout">Salir</a>
        </nav>
        <div class="mobile-nav-overlay"></div>
    </header>

    <main class="admin-container">
        <h1>${empty producto ? 'Nuevo' : 'Editar'} Producto</h1>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <form class="product-edit-form" method="post" 
              action="${pageContext.request.contextPath}/admin/producto">
            
            <input type="hidden" name="accion" value="${empty producto ? 'crear' : 'actualizar'}">
            <c:if test="${not empty producto}">
                <input type="hidden" name="id" value="${producto.id}">
            </c:if>

            <div class="form-column-left">
                <h2>Imagen</h2>
                <c:if test="${not empty producto.imagenUrl}">
                    <div class="current-images">
                        <img src="${pageContext.request.contextPath}/${producto.imagenUrl}" 
                             alt="${producto.nombre}" 
                             style="max-width: 200px;">
                    </div>
                </c:if>
                
                <div class="form-group">
                    <label for="imagenUrl">URL de la imagen *</label>
                    <input type="text"
                           id="imagenUrl"
                           name="imagenUrl"
                           value="${producto.imagenUrl}"
                           placeholder="imgs/categoria/imagen.png"
                           required>
                </div>
            </div>

            <div class="form-column-right">
                <h2>Detalles del Producto</h2>

                <div class="form-group">
                    <label for="nombre">Nombre del Producto *</label>
                    <input type="text" 
                           id="nombre" 
                           name="nombre" 
                           value="${producto.nombre}" 
                           required>
                </div>

                <div class="form-group">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion" 
                              name="descripcion" 
                              rows="4">${producto.descripcion}</textarea>
                </div>

                <div class="form-group-inline">
                    <div class="form-group">
                        <label for="precio">Precio *</label>
                        <input type="number"
                               id="precio"
                               name="precio"
                               value="${producto.precio}"
                               step="0.01"
                               min="0"
                               required>
                    </div>
                    <div class="form-group">
                        <label for="color">Color</label>
                        <input type="text"
                               id="color"
                               name="color"
                               value="${producto.color}"
                               placeholder="Negro, Blanco, Azul"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ\s,/]+"
                               title="Solo letras, espacios, comas y /">
                    </div>
                </div>

                <div class="form-group tallas-section">
                    <label>Tallas y Stock *</label>
                    <p class="form-help">Agrega al menos una talla con su stock disponible.</p>
                    <div id="tallas-container">
                        <c:choose>
                            <c:when test="${not empty tallasProducto}">
                                <c:forEach var="talla" items="${tallasProducto}" varStatus="status">
                                    <div class="talla-row">
                                        <input type="hidden" name="tallaIds[]" value="${talla.id}">
                                        <input type="text"
                                               name="tallas[]"
                                               value="${talla.talla}"
                                               placeholder="Ej: S, M, L, XL, 28, 30..."
                                               required>
                                        <input type="number"
                                               name="stocks[]"
                                               value="${talla.stock}"
                                               min="0"
                                               placeholder="Stock"
                                               required>
                                        <button type="button" class="btn-remove-talla" onclick="removeTalla(this)" title="Eliminar talla">×</button>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="talla-row">
                                    <input type="hidden" name="tallaIds[]" value="">
                                    <input type="text"
                                           name="tallas[]"
                                           placeholder="Ej: S, M, L, XL, 28, 30..."
                                           required>
                                    <input type="number"
                                           name="stocks[]"
                                           value="0"
                                           min="0"
                                           placeholder="Stock"
                                           required>
                                    <button type="button" class="btn-remove-talla" onclick="removeTalla(this)" title="Eliminar talla">×</button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <button type="button" class="btn btn-add-talla" onclick="addTalla()">+ Agregar Talla</button>
                </div>

                <div class="form-group">
                    <label for="categoriaId">Categoría *</label>
                    <select id="categoriaId" name="categoriaId" required>
                        <option value="">Seleccione una categoría</option>
                        <c:forEach var="categoria" items="${categorias}">
                            <option value="${categoria.id}" 
                                    ${producto.categoria.id == categoria.id ? 'selected' : ''}>
                                ${categoria.nombre}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary">
                    ${empty producto ? 'Crear' : 'Actualizar'} Producto
                </button>
                <a href="${pageContext.request.contextPath}/admin/producto"
                   class="btn btn-secondary"
                   style="margin-left: 10px;">
                    Cancelar
                </a>
            </div>
        </form>
    </main>

    <script>
        // Bloquear números en Color
        const colorInput = document.getElementById('color');
        if (colorInput) {
            colorInput.addEventListener('input', function(e) {
                this.value = this.value.replace(/[0-9]/g, '');
            });
        }

        // Funciones para gestionar tallas dinamicamente
        function addTalla() {
            const container = document.getElementById('tallas-container');
            const newRow = document.createElement('div');
            newRow.className = 'talla-row';
            newRow.innerHTML = `
                <input type="hidden" name="tallaIds[]" value="">
                <input type="text"
                       name="tallas[]"
                       placeholder="Ej: S, M, L, XL, 28, 30..."
                       required>
                <input type="number"
                       name="stocks[]"
                       value="0"
                       min="0"
                       placeholder="Stock"
                       required>
                <button type="button" class="btn-remove-talla" onclick="removeTalla(this)" title="Eliminar talla">×</button>
            `;
            container.appendChild(newRow);
        }

        function removeTalla(btn) {
            const container = document.getElementById('tallas-container');
            const rows = container.querySelectorAll('.talla-row');

            // Mantener al menos una fila
            if (rows.length > 1) {
                btn.closest('.talla-row').remove();
            } else {
                alert('Debe haber al menos una talla');
            }
        }

        // Validar antes de enviar el formulario
        document.querySelector('.product-edit-form').addEventListener('submit', function(e) {
            const tallasInputs = document.querySelectorAll('input[name="tallas[]"]');
            const tallas = [];

            for (let input of tallasInputs) {
                const valor = input.value.trim();
                if (valor === '') {
                    e.preventDefault();
                    alert('Todas las tallas deben tener un nombre');
                    input.focus();
                    return false;
                }
                if (tallas.includes(valor.toUpperCase())) {
                    e.preventDefault();
                    alert('No puede haber tallas duplicadas: ' + valor);
                    input.focus();
                    return false;
                }
                tallas.push(valor.toUpperCase());
            }
        });

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