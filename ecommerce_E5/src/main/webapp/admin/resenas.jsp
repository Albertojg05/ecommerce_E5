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
    <title>Moderación de Reseñas - Admin</title>
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
        <h1>Moderación de Reseñas</h1>

        <c:if test="${param.success == 'deleted'}">
            <div class="success-message">Reseña eliminada exitosamente</div>
        </c:if>

        <c:choose>
            <c:when test="${empty resenas}">
                <div class="info-message">No hay reseñas para mostrar</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="resena" items="${resenas}">
                    <div class="resena-card">
                        <div class="resena-header">
                            <div>
                                <div class="resena-usuario">${resena.usuario.nombre}</div>
                                <div class="resena-fecha">
                                    <fmt:formatDate value="${resena.fecha}" pattern="dd/MM/yyyy"/>
                                </div>
                            </div>
                            <div>
                                <button onclick="confirmarEliminar(${resena.id})" 
                                        class="btn btn-delete">Eliminar</button>
                            </div>
                        </div>
                        
                        <div class="resena-producto">
                            Producto: <strong>${resena.producto.nombre}</strong>
                        </div>
                        
                        <div class="resena-calificacion">
                            <div class="stars">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= resena.calificacion}">
                                            <span class="star-filled">★</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="star-empty">★</span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <div class="resena-comentario">
                            ${resena.comentario}
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </main>

    <!-- Modal de confirmación para eliminar reseña -->
    <div id="modal-eliminar" class="modal-overlay" style="display: none;">
        <div class="modal-content">
            <h3>Eliminar Reseña</h3>
            <p>¿Está seguro de eliminar esta reseña?</p>
            <div class="modal-actions">
                <button type="button" class="btn-modal btn-cancelar" onclick="cerrarModal()">Retroceder</button>
                <button type="button" class="btn-modal btn-confirmar" onclick="ejecutarEliminar()">Confirmar</button>
            </div>
        </div>
    </div>

    <script>
        var resenaIdEliminar = null;

        function confirmarEliminar(id) {
            resenaIdEliminar = id;
            document.getElementById('modal-eliminar').style.display = 'flex';
        }

        function cerrarModal() {
            document.getElementById('modal-eliminar').style.display = 'none';
            resenaIdEliminar = null;
        }

        function ejecutarEliminar() {
            if (resenaIdEliminar === null) return;

            var form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/admin/resenas';

            var accion = document.createElement('input');
            accion.type = 'hidden';
            accion.name = 'accion';
            accion.value = 'eliminar';
            form.appendChild(accion);

            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = resenaIdEliminar;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
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
