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
    <title>Login - Administrador</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <style>
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }
    </style>
</head>
<body>
    <header class="header-productos">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/index.html">MiTienda</a>
        </div>
        <nav class="main-nav">
            <span>Administración</span>
        </nav>
    </header>

    <main class="login-container">
        <h1 class="login-title">ADMINISTRACIÓN - LOGIN</h1>

        <!-- Mensaje de logout exitoso -->
        <c:if test="${param.logout == 'true'}">
            <div class="success-message">
                Sesión cerrada correctamente.
            </div>
        </c:if>

        <!-- Mensaje de error -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>

        <form class="login-form" method="post" action="${pageContext.request.contextPath}/admin/login">
            <div class="form-group">
                <label for="correo">Correo Electrónico</label>
                <input type="email" 
                       id="correo" 
                       name="correo" 
                       class="form-input" 
                       value="${correo}" 
                       required 
                       autofocus>
            </div>

            <div class="form-group">
                <label for="contrasena">Contraseña</label>
                <input type="password" 
                       id="contrasena" 
                       name="contrasena" 
                       class="form-input" 
                       required>
            </div>

            <button class="login-btn" type="submit">INGRESAR</button>
        </form>

        <p style="text-align: center; margin-top: 20px; color: #666;">
            <small>Panel de administración - Solo para usuarios autorizados</small>
        </p>
    </main>
</body>
</html>
