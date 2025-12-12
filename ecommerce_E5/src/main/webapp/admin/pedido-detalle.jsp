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
    <title>Detalle del Pedido - Admin</title>
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
        <h1>Detalle del Pedido ${pedido.numeroPedido}</h1>

        <c:if test="${param.success == 'updated'}">
            <div class="success-message">Estado actualizado exitosamente</div>
        </c:if>

        <c:if test="${param.success == 'cancelled'}">
            <div class="success-message">Pedido cancelado exitosamente</div>
        </c:if>

        <!-- Información del Cliente -->
        <div class="pedido-section">
            <h2>Información del Cliente</h2>
            <div class="info-grid">
                <div class="info-item">
                    <label>Nombre:</label>
                    <p>${pedido.usuario.nombre}</p>
                </div>
                <div class="info-item">
                    <label>Correo:</label>
                    <p>${pedido.usuario.correo}</p>
                </div>
                <div class="info-item">
                    <label>Teléfono:</label>
                    <p>${pedido.usuario.telefono}</p>
                </div>
            </div>
        </div>

        <!-- Información del Pedido -->
        <div class="pedido-section">
            <h2>Información del Pedido</h2>
            <div class="info-grid">
                <div class="info-item">
                    <label>Fecha:</label>
                    <p><fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy HH:mm"/></p>
                </div>
                <div class="info-item">
                    <label>Estado:</label>
                    <p><span class="badge badge-${pedido.estado.name().toLowerCase()}">${pedido.estado}</span></p>
                </div>
                <div class="info-item">
                    <label>Total:</label>
                    <p><strong>$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></strong></p>
                </div>
            </div>
        </div>

        <!-- Dirección de Envío -->
        <div class="pedido-section">
            <h2>Dirección de Envío</h2>
            <div class="pedido-info">
                <p><strong>Calle:</strong> ${pedido.direccionEnvio.calle}</p>
                <p><strong>Ciudad:</strong> ${pedido.direccionEnvio.ciudad}</p>
                <p><strong>Estado:</strong> ${pedido.direccionEnvio.estado}</p>
                <p><strong>Código Postal:</strong> ${pedido.direccionEnvio.codigoPostal}</p>
            </div>
        </div>

        <!-- Información de Pago -->
        <div class="pedido-section">
            <h2>Información de Pago</h2>
            <div class="info-grid">
                <div class="info-item">
                    <label>Método:</label>
                    <p>${pedido.pago.metodo}</p>
                </div>
                <div class="info-item">
                    <label>Estado:</label>
                    <p><span class="badge badge-${pedido.pago.estado.name().toLowerCase()}">${pedido.pago.estado}</span></p>
                </div>
                <div class="info-item">
                    <label>Monto:</label>
                    <p>$<fmt:formatNumber value="${pedido.pago.monto}" pattern="#,##0.00"/></p>
                </div>
            </div>
        </div>

        <!-- Productos del Pedido -->
        <div class="pedido-section">
            <h2>Productos</h2>
            <table class="product-table">
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Precio Unitario</th>
                        <th>Cantidad</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="detalle" items="${pedido.detalles}">
                        <tr>
                            <td>${detalle.producto.nombre}</td>
                            <td>$<fmt:formatNumber value="${detalle.precioUnitario}" pattern="#,##0.00"/></td>
                            <td>${detalle.cantidad}</td>
                            <td>$<fmt:formatNumber value="${detalle.precioUnitario * detalle.cantidad}" pattern="#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Actualizar Estado -->
        <c:if test="${pedido.estado != 'ENTREGADO' && pedido.estado != 'CANCELADO'}">
            <form class="form-update-estado" method="post" 
                  action="${pageContext.request.contextPath}/admin/pedidos">
                <input type="hidden" name="accion" value="actualizarEstado">
                <input type="hidden" name="id" value="${pedido.id}">
                
                <h2>Actualizar Estado del Pedido</h2>
                <div class="form-group">
                    <label for="nuevoEstado">Nuevo Estado:</label>
                    <select id="nuevoEstado" name="nuevoEstado" required>
                        <c:forEach var="estado" items="${estados}">
                            <option value="${estado}" ${pedido.estado == estado ? 'selected' : ''}>
                                ${estado}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <button type="submit" class="btn btn-primary">Actualizar Estado</button>
                
                <c:if test="${pedido.estado != 'CANCELADO'}">
                    <button type="button" 
                            onclick="confirmarCancelacion()" 
                            class="btn btn-delete" 
                            style="margin-left: 10px;">
                        Cancelar Pedido
                    </button>
                </c:if>
            </form>
        </c:if>

        <a href="${pageContext.request.contextPath}/admin/pedidos" 
           class="btn" 
           style="background-color: #95a5a6; color: white; margin-top: 20px; display: inline-block;">
            Volver a Pedidos
        </a>
    </main>

    <script>
        function confirmarCancelacion() {
            if (confirm('¿Está seguro de cancelar este pedido? Esta acción restaurará el stock de los productos.')) {
                var form = document.createElement('form');
                form.method = 'post';
                form.action = '${pageContext.request.contextPath}/admin/pedidos';

                var accion = document.createElement('input');
                accion.type = 'hidden';
                accion.name = 'accion';
                accion.value = 'cancelar';
                form.appendChild(accion);

                var id = document.createElement('input');
                id.type = 'hidden';
                id.name = 'id';
                id.value = '${pedido.id}';
                form.appendChild(id);

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