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
        <title>Gestión de Productos - Admin</title>
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
            <div class="admin-header">
                <h1>Gestionar Productos</h1>
                <a href="${pageContext.request.contextPath}/admin/producto?accion=nuevo" 
                   class="btn btn-primary">+ Agregar Nuevo Producto</a>
            </div>

            <c:if test="${param.success == 'created'}">
                <div class="success-message">Producto creado exitosamente</div>
            </c:if>

            <c:if test="${param.success == 'updated'}">
                <div class="success-message">Producto actualizado exitosamente</div>
            </c:if>

            <c:if test="${param.success == 'deleted'}">
                <div class="success-message">Producto eliminado exitosamente</div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <table class="product-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Imagen</th>
                        <th>Nombre</th>
                        <th>Categoría</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="producto" items="${productos}">
                        <tr>
                            <td data-label="ID">${producto.id}</td>
                            <td data-label="Imagen">
                                <c:choose>
                                    <c:when test="${not empty producto.imagenUrl}">
                                        <img src="${pageContext.request.contextPath}/${producto.imagenUrl}"
                                             alt="${producto.nombre}"
                                             style="width: 60px; height: auto;">
                                    </c:when>
                                    <c:otherwise>
                                        <span>Sin imagen</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td data-label="Nombre">${producto.nombre}</td>
                            <td data-label="Categoría">${producto.categoria.nombre}</td>
                            <td data-label="Precio">$<fmt:formatNumber value="${producto.precio}" pattern="#,##0.00"/></td>
                            <td data-label="Stock">${producto.existencias}</td>
                            <td data-label="Acciones">
                                <a href="${pageContext.request.contextPath}/admin/producto?accion=editar&id=${producto.id}"
                                   class="btn btn-edit">Editar</a>
                                <button onclick="confirmarEliminar(${producto.id}, '${producto.nombre}')"
                                        class="btn btn-delete">Eliminar</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </main>

        <!-- Modal de confirmación para eliminar producto -->
        <div id="modal-eliminar" class="modal-overlay" style="display: none;">
            <div class="modal-content">
                <h3>Eliminar Producto</h3>
                <p>¿Está seguro de eliminar este producto?</p>
                <div class="modal-actions">
                    <button type="button" class="btn-modal btn-cancelar" onclick="cerrarModal()">Retroceder</button>
                    <button type="button" class="btn-modal btn-confirmar" onclick="ejecutarEliminar()">Confirmar</button>
                </div>
            </div>
        </div>

        <script>
            var productoIdEliminar = null;

            function confirmarEliminar(id, nombre) {
                productoIdEliminar = id;
                document.getElementById('modal-eliminar').style.display = 'flex';
            }

            function cerrarModal() {
                document.getElementById('modal-eliminar').style.display = 'none';
                productoIdEliminar = null;
            }

            function ejecutarEliminar() {
                if (productoIdEliminar === null) return;
                window.location.href = '${pageContext.request.contextPath}/admin/producto?accion=eliminar&id=' + productoIdEliminar;
            }

            // Cerrar modal al hacer clic fuera
            document.getElementById('modal-eliminar').addEventListener('click', function(e) {
                if (e.target === this) {
                    cerrarModal();
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