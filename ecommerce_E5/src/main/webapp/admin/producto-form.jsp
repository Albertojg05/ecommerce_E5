<%-- 
    @author Alberto Jiménez García 252595
    Rene Ezequiel Figueroa Lopez 228691
    Freddy Alí Castro Román 252191
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${empty producto ? 'Nuevo' : 'Editar'} Producto - Admin</title>
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
            <h1>${empty producto ? 'Nuevo' : 'Editar'} Producto</h1>

            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <div class="product-edit-form">
                <div>
                    <c:if test="${not empty producto.imagenUrl}">
                        <div class="current-images">
                            <img src="${producto.imagenUrl}" alt="Preview">
                        </div>
                    </c:if>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/admin/productos">
                    <input type="hidden" name="accion" value="${empty producto ? 'crear' : 'actualizar'}">

                    <c:if test="${not empty producto}">
                        <input type="hidden" name="id" value="${producto.id}">
                    </c:if>

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
                                   step="0.01" 
                                   min="0" 
                                   value="${producto.precio}" 
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="existencias">Stock/Existencias *</label>
                            <input type="number" 
                                   id="existencias" 
                                   name="existencias" 
                                   min="0" 
                                   value="${producto.existencias}" 
                                   required>
                        </div>
                    </div>

                    <div class="form-group-inline">
                        <div class="form-group">
                            <label for="talla">Talla</label>
                            <input type="text" 
                                   id="talla" 
                                   name="talla" 
                                   placeholder="Ej: S, M, L, XL" 
                                   value="${producto.talla}">
                        </div>

                        <div class="form-group">
                            <label for="color">Color</label>
                            <input type="text" 
                                   id="color" 
                                   name="color" 
                                   placeholder="Ej: Negro, Blanco" 
                                   value="${producto.color}">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="categoriaId">Categoría *</label>
                        <select id="categoriaId" name="categoriaId" required>
                            <option value="">-- Seleccionar Categoría --</option>
                            <c:forEach var="categoria" items="${categorias}">
                                <option value="${categoria.id}" 
                                        ${producto.categoria.id == categoria.id ? 'selected' : ''}>
                                    ${categoria.nombre}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="imagenUrl">URL de la Imagen</label>
                        <input type="url" 
                               id="imagenUrl" 
                               name="imagenUrl" 
                               placeholder="https://ejemplo.com/imagen.jpg" 
                               value="${producto.imagenUrl}">
                        <small style="color: #666; display: block; margin-top: 5px;">
                            Ingresa la URL de la imagen del producto
                        </small>
                    </div>

                    <div style="margin-top: 30px; display: flex; gap: 10px;">
                        <button type="submit" class="btn btn-primary">
                            ${empty producto ? 'Crear Producto' : 'Guardar Cambios'}
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/productos" 
                           class="btn" 
                           style="background: #6c757d; color: white; text-decoration: none; display: inline-block; text-align: center;">
                            Cancelar
                        </a>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>
