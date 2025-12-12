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
        <title>Editar Perfil - MiTienda</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cliente.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paginas/account.css">
    </head>
    <body class="edit-profile-page">
        <header class="header-productos">
            <div class="logo"><a href="${pageContext.request.contextPath}/index">MiTienda</a></div>
            <button class="mobile-menu-btn" aria-label="Menu">
                <span></span>
                <span></span>
                <span></span>
            </button>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/cliente/productos">Tienda</a>
                <a href="${pageContext.request.contextPath}/cliente/carrito">Carrito</a>
                <a href="${pageContext.request.contextPath}/cliente/cuenta">Mi Cuenta</a>
                <a href="${pageContext.request.contextPath}/logout">Cerrar Sesion</a>
            </nav>
            <div class="mobile-nav-overlay"></div>
        </header>
        <main class="account-page account-edit-page">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <c:if test="${param.mensaje == 'direccionAgregada'}">
                <div class="alert alert-success">Dirección agregada correctamente.</div>
            </c:if>
            <c:if test="${param.mensaje == 'direccionActualizada'}">
                <div class="alert alert-success">Dirección actualizada correctamente.</div>
            </c:if>
            <c:if test="${param.mensaje == 'direccionEliminada'}">
                <div class="alert alert-success">Dirección eliminada correctamente.</div>
            </c:if>

            <div class="account-container">
                <aside class="account-sidebar">
                    <div class="user-info">
                        <div class="user-avatar">
                            <span>${usuario.nombre.substring(0,1).toUpperCase()}</span>
                        </div>
                        <h2>${usuario.nombre}</h2>
                        <p class="user-email">${usuario.correo}</p>
                    </div>
                    <nav class="account-nav">
                        <a href="${pageContext.request.contextPath}/cliente/cuenta">Mis Pedidos</a>
                        <a href="${pageContext.request.contextPath}/cliente/cuenta?accion=editar" class="active">Editar Perfil</a>
                        <a href="${pageContext.request.contextPath}/logout" class="logout-link">Cerrar Sesión</a>
                    </nav>
                </aside>

                <section class="account-content">
                    <h1>Editar Perfil</h1>

                    <form class="edit-form" action="${pageContext.request.contextPath}/cliente/cuenta" method="post">
                        <input type="hidden" name="accion" value="actualizar">

                        <div class="form-section">
                            <h3>Información Personal</h3>
                            <div class="form-group">
                                <label for="nombre">Nombre completo</label>
                                <input type="text" id="nombre" name="nombre" value="${usuario.nombre}" required class="form-input"
                                       pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü\s]+" title="Solo letras y espacios"
                                       oninput="this.value = this.value.replace(/[0-9]/g, '')">
                            </div>
                            <div class="form-group">
                                <label for="email">Correo electrónico</label>
                                <input type="email" id="email" name="email" value="${usuario.correo}" required class="form-input">
                            </div>
                            <div class="form-group">
                                <label for="telefono">Teléfono (10 dígitos)</label>
                                <input type="tel" id="telefono" name="telefono" value="${usuario.telefono}"
                                       class="form-input" pattern="[0-9]{10}" maxlength="10" inputmode="numeric"
                                       oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                            </div>
                        </div>

                        <div class="form-section">
                            <h3>Cambiar Contraseña</h3>
                            <p class="form-hint">Deja en blanco si no deseas cambiar la contraseña.</p>
                            <div class="form-group">
                                <label for="contrasenaActual">Contraseña actual</label>
                                <input type="password" id="contrasenaActual" name="contrasenaActual" class="form-input">
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="nuevaContrasena">Nueva contraseña</label>
                                    <input type="password" id="nuevaContrasena" name="nuevaContrasena" class="form-input">
                                </div>
                                <div class="form-group">
                                    <label for="confirmarContrasena">Confirmar contraseña</label>
                                    <input type="password" id="confirmarContrasena" name="confirmarContrasena" class="form-input">
                                </div>
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/cliente/cuenta" class="btn-cancel">Cancelar</a>
                            <button type="submit" class="btn-save">Guardar Cambios</button>
                        </div>
                    </form>

                    <!-- Sección de Direcciones de Envío -->
                    <div class="direcciones-section">
                        <h2>Direcciones de Envío</h2>
                        <p class="direcciones-info">Máximo ${maxDirecciones} direcciones. Tienes ${direcciones.size()} registrada(s).</p>

                        <!-- Direcciones existentes -->
                        <c:if test="${not empty direcciones}">
                            <c:forEach var="dir" items="${direcciones}" varStatus="status">
                                <div class="direccion-card">
                                    <div class="direccion-header" onclick="toggleDireccion(${dir.id})">
                                        <span class="direccion-titulo">Dirección ${status.index + 1}</span>
                                        <span class="direccion-preview">${dir.calle}, ${dir.ciudad}</span>
                                        <span class="direccion-toggle" id="toggle-${dir.id}">▼</span>
                                    </div>
                                    <div class="direccion-contenido" id="direccion-${dir.id}" style="display: none;">
                                        <form action="${pageContext.request.contextPath}/cliente/cuenta" method="post">
                                            <input type="hidden" name="accion" value="editarDireccion">
                                            <input type="hidden" name="direccionId" value="${dir.id}">
                                            <div class="form-group">
                                                <label>Calle y número</label>
                                                <input type="text" name="calle" value="${dir.calle}" required class="form-input">
                                            </div>
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label>Ciudad</label>
                                                    <input type="text" name="ciudad" value="${dir.ciudad}" required class="form-input">
                                                </div>
                                                <div class="form-group">
                                                    <label>Estado</label>
                                                    <input type="text" name="estado" value="${dir.estado}" required class="form-input">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label>Código Postal</label>
                                                <input type="text" name="codigoPostal" value="${dir.codigoPostal}" required
                                                       class="form-input" pattern="[0-9]{5}" maxlength="5"
                                                       oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                                            </div>
                                            <div class="direccion-actions">
                                                <button type="submit" class="btn-save-small">Guardar Cambios</button>
                                                <button type="button" class="btn-delete" onclick="confirmarEliminarDireccion(${dir.id})">Eliminar</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>

                        <!-- Botón/Formulario para agregar nueva dirección -->
                        <c:if test="${puedeAgregarDireccion}">
                            <div class="direccion-card nueva-direccion">
                                <div class="direccion-header" onclick="toggleNuevaDireccion()">
                                    <span class="direccion-titulo">+ Agregar Nueva Dirección</span>
                                    <span class="direccion-toggle" id="toggle-nueva">▼</span>
                                </div>
                                <div class="direccion-contenido" id="nueva-direccion" style="display: none;">
                                    <form action="${pageContext.request.contextPath}/cliente/cuenta" method="post">
                                        <input type="hidden" name="accion" value="agregarDireccion">
                                        <div class="form-group">
                                            <label>Calle y número *</label>
                                            <input type="text" name="calle" required class="form-input">
                                        </div>
                                        <div class="form-row">
                                            <div class="form-group">
                                                <label>Ciudad *</label>
                                                <input type="text" name="ciudad" required class="form-input">
                                            </div>
                                            <div class="form-group">
                                                <label>Estado *</label>
                                                <input type="text" name="estado" required class="form-input">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label>Código Postal *</label>
                                            <input type="text" name="codigoPostal" required class="form-input"
                                                   pattern="[0-9]{5}" maxlength="5"
                                                   oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                                        </div>
                                        <div class="direccion-actions">
                                            <button type="submit" class="btn-save-small">Agregar Dirección</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${not puedeAgregarDireccion}">
                            <p class="limite-direcciones">Has alcanzado el límite de ${maxDirecciones} direcciones. Elimina una para agregar otra.</p>
                        </c:if>
                    </div>

                    <!-- Formulario oculto para eliminar dirección -->
                    <form id="form-eliminar-direccion" action="${pageContext.request.contextPath}/cliente/cuenta" method="post" style="display: none;">
                        <input type="hidden" name="accion" value="eliminarDireccion">
                        <input type="hidden" name="direccionId" id="direccionIdEliminar" value="">
                    </form>
                </section>
            </div>
        </main>

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

            // Toggle para direcciones existentes
            function toggleDireccion(id) {
                const contenido = document.getElementById('direccion-' + id);
                const toggle = document.getElementById('toggle-' + id);
                if (contenido.style.display === 'none') {
                    contenido.style.display = 'block';
                    toggle.textContent = '▲';
                } else {
                    contenido.style.display = 'none';
                    toggle.textContent = '▼';
                }
            }

            // Toggle para nueva dirección
            function toggleNuevaDireccion() {
                const contenido = document.getElementById('nueva-direccion');
                const toggle = document.getElementById('toggle-nueva');
                if (contenido.style.display === 'none') {
                    contenido.style.display = 'block';
                    toggle.textContent = '▲';
                } else {
                    contenido.style.display = 'none';
                    toggle.textContent = '▼';
                }
            }

            // Confirmar eliminación de dirección
            function confirmarEliminarDireccion(id) {
                if (confirm('¿Estás seguro de que deseas eliminar esta dirección?')) {
                    document.getElementById('direccionIdEliminar').value = id;
                    document.getElementById('form-eliminar-direccion').submit();
                }
            }

            // Abrir formulario de nueva dirección si viene el parámetro
            document.addEventListener('DOMContentLoaded', function() {
                const urlParams = new URLSearchParams(window.location.search);
                if (urlParams.get('nuevaDireccion') === 'true') {
                    // Abrir el formulario de nueva dirección
                    toggleNuevaDireccion();
                    // Scroll hacia la sección de direcciones
                    const direccionesSection = document.querySelector('.direcciones-section');
                    if (direccionesSection) {
                        direccionesSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }
                }
            });
        </script>
    </body>
</html>
