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
                        <label for="existencias">Stock *</label>
                        <input type="number" 
                               id="existencias" 
                               name="existencias" 
                               value="${producto.existencias}" 
                               min="0" 
                               required>
                    </div>
                </div>

                <div class="form-group-inline">
                    <div class="form-group">
                        <label for="talla">Talla *</label>
                        <input type="text"
                               id="talla"
                               name="talla"
                               value="${producto.talla}"
                               placeholder="XS, S, M, L, XL"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ\s,/]+"
                               title="Solo letras, espacios, comas y /"
                               required>
                    </div>
                    <div class="form-group">
                        <label for="color">Color *</label>
                        <input type="text"
                               id="color"
                               name="color"
                               value="${producto.color}"
                               placeholder="Negro, Blanco, Azul"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ\s,/]+"
                               title="Solo letras, espacios, comas y /"
                               required>
                    </div>
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
        // Bloquear números en Talla y Color
        document.getElementById('talla').addEventListener('input', function(e) {
            this.value = this.value.replace(/[0-9]/g, '');
        });

        document.getElementById('color').addEventListener('input', function(e) {
            this.value = this.value.replace(/[0-9]/g, '');
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