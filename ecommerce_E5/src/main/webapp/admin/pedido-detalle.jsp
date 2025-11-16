<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Detalle del Pedido - Admin</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-common.css">
    </head>
    <body>
        <header class="header-productos">
            <div class="logo">MiTienda Admin</div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/admin/pedidos">Pedidos</a>
                <a href="${pageContext.request.contextPath}/admin/resenas">Reseñas</a>
                <a href="${pageContext.request.contextPath}/admin/logout">Salir</a>
            </nav>
        </header>

        <main class="admin-container">
            <h1>Detalle del Pedido</h1>

            <c:if test="${param.success == 'updated'}">
                <div class="success-message">Estado actualizado exitosamente.</div>
            </c:if>
            <c:if test="${param.success == 'cancelled'}">
                <div class="success-message">Pedido cancelado exitosamente.</div>
            </c:if>

            <c:if test="${empty pedido}">
                <p>Pedido no encontrado.</p>
                <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn btn-primary">Volver a Pedidos</a>
            </c:if>

            <c:if test="${not empty pedido}">
                <div class="pedido-section">
                    <h2>Información General</h2>
                    <div class="pedido-info">
                        <div class="info-grid">
                            <div class="info-item">
                                <label>Número de Pedido:</label>
                                <strong>${pedido.numeroPedido}</strong>
                            </div>
                            <div class="info-item">
                                <label>Fecha:</label>
                                <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy HH:mm"/>
                            </div>
                            <div class="info-item">
                                <label>Estado:</label>
                                <span class="badge badge-${pedido.estado.name().toLowerCase()}">
                                    ${pedido.estado}
                                </span>
                            </div>
                            <div class="info-item">
                                <label>Total:</label>
                                <strong style="font-size: 18px;">$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></strong>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pedido-section">
                    <h2>Cliente</h2>
                    <div class="pedido-info">
                        <div class="info-grid">
                            <div class="info-item">
                                <label>Nombre:</label>
                                ${pedido.usuario.nombre}
                            </div>
                            <div class="info-item">
                                <label>Correo:</label>
                                ${pedido.usuario.correo}
                            </div>
                            <div class="info-item">
                                <label>Teléfono:</label>
                                ${pedido.usuario.telefono}
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pedido-section">
                    <h2>Dirección de Envío</h2>
                    <div class="pedido-info">
                        <p>
                            ${pedido.direccionEnvio.calle}<br>
                            ${pedido.direccionEnvio.ciudad}, ${pedido.direccionEnvio.estado}<br>
                            C.P. ${pedido.direccionEnvio.codigoPostal}
                        </p>
                    </div>
                </div>

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
                                    <td>
                                        <strong>${detalle.producto.nombre}</strong><br>
                                        <small>${detalle.producto.color} - ${detalle.producto.talla}</small>
                                    </td>
                                    <td>$<fmt:formatNumber value="${detalle.precioUnitario}" pattern="#,##0.00"/></td>
                                    <td>${detalle.cantidad}</td>
                                    <td>
                                        <strong>$<fmt:formatNumber value="${detalle.precioUnitario * detalle.cantidad}" pattern="#,##0.00"/></strong>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr style="background: #f0f0f0; font-weight: bold;">
                                <td colspan="3" style="text-align: right;">TOTAL:</td>
                                <td>$<fmt:formatNumber value="${pedido.total}" pattern="#,##0.00"/></td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <c:if test="${not empty pedido.pago}">
                    <div class="pedido-section">
                        <h2>Información de Pago</h2>
                        <div class="pedido-info">
                            <div class="info-grid">
                                <div class="info-item">
                                    <label>Método de Pago:</label>
                                    ${pedido.pago.metodo}
                                </div>
                                <div class="info-item">
                                    <label>Estado del Pago:</label>
                                    <span class="badge badge-${pedido.pago.estado.name().toLowerCase()}">
                                        ${pedido.pago.estado}
                                    </span>
                                </div>
                                <div class="info-item">
                                    <label>Monto:</label>
                                    $<fmt:formatNumber value="${pedido.pago.monto}" pattern="#,##0.00"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <div class="form-update-estado">
                    <h3>Actualizar Estado del Pedido</h3>
                    <form method="post" action="${pageContext.request.contextPath}/admin/pedidos" style="display: flex; gap: 15px; align-items: center; margin-top: 15px;">
                        <input type="hidden" name="accion" value="actualizarEstado">
                        <input type="hidden" name="id" value="${pedido.id}">

                        <label for="nuevoEstado" style="font-weight: 600;">Nuevo Estado:</label>
                        <select id="nuevoEstado" name="nuevoEstado" required>
                            <c:forEach var="estado" items="${estados}">
                                <option value="${estado}" ${pedido.estado == estado ? 'selected' : ''}>
                                    ${estado}
                                </option>
                            </c:forEach>
                        </select>

                        <button type="submit" class="btn btn-primary">Actualizar Estado</button>

                        <c:if test="${pedido.estado != 'CANCELADO' && pedido.estado != 'ENTREGADO'}">
                            <button type="button" 
                                    onclick="if (confirm('¿Cancelar este pedido?')) {
                                            document.getElementById('formCancelar').submit();
                                        }" 
                                    class="btn btn-delete">
                                Cancelar Pedido
                            </button>
                        </c:if>
                    </form>

                    <form id="formCancelar" method="post" action="${pageContext.request.contextPath}/admin/pedidos" style="display: none;">
                        <input type="hidden" name="accion" value="cancelar">
                        <input type="hidden" name="id" value="${pedido.id}">
                    </form>
                </div>

                <div style="margin-top: 30px;">
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn" style="background: #6c757d; color: white; text-decoration: none; display: inline-block; padding: 10px 18px;">
                        ← Volver a Pedidos
                    </a>
                </div>
            </c:if>
        </main>
    </body>
</html>