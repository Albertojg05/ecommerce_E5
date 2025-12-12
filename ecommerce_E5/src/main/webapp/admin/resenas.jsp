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
        <div class="logo">MiTienda Admin</div>
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

    <script>
        function confirmarEliminar(id) {
            if (confirm('¿Está seguro de eliminar esta reseña?')) {
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
                idInput.value = id;
                form.appendChild(idInput);

                document.body.appendChild(form);
                form.submit();
            }
        }

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
