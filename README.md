# Tienda de Ropa E5

Proyecto final para la materia de **Aplicaciones Web**

## Equipo

Alberto Jiménez García          252595
Rene Ezequiel Figueroa Lopez    228691
Freddy Alí Castro Román         252191

## Descripción

Tienda de ropa en línea donde los clientes pueden ver el catálogo de productos, agregarlos al carrito y realizar compras. También tiene un panel de administración para gestionar los productos, pedidos y ver las reseñas de los clientes.

El proyecto sigue el patrón MVC y usa JPA con Hibernate para la persistencia de datos.

## Tecnologías

- Java 11
- Jakarta EE 10 (Servlets, JSP, JSTL)
- Hibernate 6.4 / JPA
- MySQL 8
- Maven
- Apache Tomcat 10+
- BCrypt (para las contraseñas)

## Requisitos

- JDK 11 o superior
- Apache NetBeans 17+ (nosotros usamos el 27)
- MySQL Server 8.0
- Apache Tomcat 10.1

## Configuración

### 1. Base de datos

Ejecutar el script `ecommerce_db.sql` en MySQL. Este crea la base de datos, las tablas, vistas, procedimientos y datos de prueba.

```sql
source ecommerce_db.sql;
```

### 2. Configurar conexión

Editar el archivo `ecommerce_E5/src/main/resources/META-INF/persistence.xml` y cambiar el usuario y contraseña de MySQL:

```xml
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="ITSON"/>
```

### 3. Ejecutar

Abrir el proyecto en NetBeans, hacer clic derecho en el proyecto y seleccionar "Run". Se desplegará en Tomcat automáticamente.

La aplicación estará en: `http://localhost:8080/ecommerce_E5/`

## Estructura del proyecto

El código Java está en `src/main/java/com/mycompany/ecommerce_e5/` y se divide en:

- **dominio/** - Las entidades de JPA (Usuario, Producto, Pedido, etc)
- **dao/** - Clases para acceder a la base de datos
- **bo/** - Lógica de negocio y validaciones
- **servlets/admin/** - Controladores del panel de administración
- **servlets/cliente/** - Controladores de la tienda
- **filters/** - Filtros para verificar si el usuario está logueado
- **excepciones/** - Excepciones propias del proyecto
- **util/** - Clases de utilería (conexión JPA, validaciones, etc)

Las vistas JSP están en `src/main/webapp/` separadas en carpetas `admin/` y `cliente/`.

Los diagramas están en las carpetas `Diagrama/` y `MER/` en la raíz del repo.

## Funcionalidades

### Tienda (Cliente)
- Ver catálogo de productos
- Filtrar por categoría
- Buscar productos por nombre
- Ver detalle de producto con reseñas
- Carrito de compras
- Proceso de checkout
- Registro e inicio de sesión
- Ver y editar datos de la cuenta

### Panel Admin
- Dashboard con resumen
- CRUD de productos
- Ver y gestionar pedidos
- Ver reseñas de clientes

## Usuarios de prueba

Los usuarios ya vienen en el script de la base de datos:

**Administrador:**
- Correo: `admin@mitienda.com`
- Contraseña: `admin123`

**Clientes:**
- Correo: `maria@email.com` / Contraseña: `admin123`
- Correo: `ana@email.com` / Contraseña: `admin123`
- Correo: `laura@email.com` / Contraseña: `admin123`

## Rutas principales

**Para clientes:**
- `/` - Inicio
- `/cliente/productos` - Ver todos los productos
- `/cliente/carrito` - Carrito
- `/cliente/checkout` - Pagar
- `/cliente/cuenta` - Mi cuenta
- `/login` y `/register` - Para iniciar sesión o registrarse

**Para administradores:**
- `/admin/login` - Entrar al panel
- `/admin/dashboard` - Ver resumen
- `/admin/productos` - Agregar/editar/eliminar productos
- `/admin/pedidos` - Ver pedidos de clientes

## Diagramas

El diagrama MER y el diagrama de clases están en las carpetas `MER/` y `Diagrama/` respectivamente.

### Tablas principales
- `usuario` - Clientes y administradores
- `producto` - Artículos de la tienda
- `categoria` - Categorías de ropa (Vestidos, Blusas, etc.)
- `pedido` - Órdenes de compra
- `detalle_pedido` - Productos de cada pedido
- `direccion` - Direcciones de envío
- `pago` - Información de pagos
- `resena` - Opiniones de los clientes

## Notas

- El carrito se guarda en la sesión del usuario
- Las contraseñas se guardan hasheadas con BCrypt
- Envío gratis a partir de $1,500
- Máximo 10 unidades por producto en el carrito

---
