<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Moderación de Reseñas - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <style>
        .resena-actions {
            display: flex;
            gap: 8px;
        }
        .btn-edit {
            background-color: #222;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-edit:hover {
            background-color: #444;
        }
        .star-rating {
            display: flex;
            flex-direction: row-reverse;
            justify-content: flex-end;
            gap: 4px;
        }
        .star-rating input {
            display: none;
        }
        .star-rating label {
            font-size: 28px;
            color: #ddd;
            cursor: pointer;
            transition: color 0.2s;
        }
        .star-rating input:checked ~ label,
        .star-rating label:hover,
        .star-rating label:hover ~ label {
            color: #222;
        }
        .form-group {
            margin-bottom: 16px;
        }
        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
        }
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            resize: vertical;
            font-family: inherit;
        }
        #modal-editar .modal-content {
            max-width: 450px;
        }
    </style>
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
        <c:if test="${param.success == 'edited'}">
            <div class="success-message">Reseña actualizada exitosamente</div>
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
                            <div class="resena-actions">
                                <button class="btn btn-edit btn-editar-resena"
                                        data-id="${resena.id}"
                                        data-calificacion="${resena.calificacion}"
                                        data-comentario="${fn:escapeXml(resena.comentario)}">Editar</button>
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

    <!-- Modal de edición de reseña -->
    <div id="modal-editar" class="modal-overlay" style="display: none;">
        <div class="modal-content">
            <h3>Editar Reseña</h3>
            <form id="form-editar" method="post" action="${pageContext.request.contextPath}/admin/resenas">
                <input type="hidden" name="accion" value="editar">
                <input type="hidden" name="id" id="editar-id">

                <div class="form-group">
                    <label>Calificación:</label>
                    <div class="star-rating">
                        <input type="radio" name="calificacion" value="5" id="star5"><label for="star5">★</label>
                        <input type="radio" name="calificacion" value="4" id="star4"><label for="star4">★</label>
                        <input type="radio" name="calificacion" value="3" id="star3"><label for="star3">★</label>
                        <input type="radio" name="calificacion" value="2" id="star2"><label for="star2">★</label>
                        <input type="radio" name="calificacion" value="1" id="star1"><label for="star1">★</label>
                    </div>
                </div>

                <div class="form-group">
                    <label for="editar-comentario">Comentario:</label>
                    <textarea name="comentario" id="editar-comentario" rows="4"
                              placeholder="Mínimo 10 caracteres si se proporciona"></textarea>
                </div>

                <div class="modal-actions">
                    <button type="button" class="btn-modal btn-cancelar" onclick="cerrarModalEditar()">Cancelar</button>
                    <button type="submit" class="btn-modal btn-confirmar">Guardar</button>
                </div>
            </form>
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

        // Funciones para modal de edición
        function cerrarModalEditar() {
            document.getElementById('modal-editar').style.display = 'none';
            document.getElementById('form-editar').reset();
        }

        // Event listeners para botones de editar
        document.querySelectorAll('.btn-editar-resena').forEach(function(btn) {
            btn.addEventListener('click', function() {
                var id = this.getAttribute('data-id');
                var calificacion = this.getAttribute('data-calificacion');
                var comentario = this.getAttribute('data-comentario') || '';

                document.getElementById('editar-id').value = id;
                document.getElementById('editar-comentario').value = comentario;

                // Seleccionar la calificación actual
                var radioBtn = document.getElementById('star' + calificacion);
                if (radioBtn) {
                    radioBtn.checked = true;
                }

                document.getElementById('modal-editar').style.display = 'flex';
            });
        });

        // Cerrar modal al hacer clic fuera
        document.getElementById('modal-eliminar').addEventListener('click', function(e) {
            if (e.target === this) {
                cerrarModal();
            }
        });

        document.getElementById('modal-editar').addEventListener('click', function(e) {
            if (e.target === this) {
                cerrarModalEditar();
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
