# Tienda de Ropa E5

Proyecto final para la materia de **Aplicaciones Web** - ITSON Plan 2023

## Equipo

Alberto Jiménez García          252595
Rene Ezequiel Figueroa Lopez    228691
Freddy Alí Castro Román         252191

## Descripción

Tienda de ropa en línea donde los clientes pueden ver el catálogo de productos, agregarlos al carrito y realizar compras. También cuenta con un panel de administración para gestionar productos, pedidos y reseñas.

El proyecto sigue el patrón **MVC** (Model-View-Controller) y usa **JPA con Hibernate** para la persistencia de datos.

## Tecnologías

- Java 11
- Jakarta EE 10 (Servlets, JSP, JSTL)
- Hibernate 6.4 / JPA
- MySQL 8.0
- Apache Tomcat 10.1+
- Jersey (JAX-RS) 3.1.3
- BCrypt 0.4 (para las contraseñas)

## Requisitos

- JDK 11 o superior
- Apache NetBeans 17+
- MySQL Server 8.0
- Apache Tomcat 10.1

---

## Configuración e Instalación

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/ecommerce_E5.git
cd ecommerce_E5
```

### Paso 2: Configurar la base de datos

Ejecutar el script `ecommerce_db.sql` en MySQL. Este crea la base de datos, las tablas, vistas, procedimientos y datos de prueba.

```sql
source ecommerce_db.sql;
```

Este script crea:
- Base de datos `ecommerce_db`
- Todas las tablas necesarias
- Vistas, procedimientos almacenados y triggers
- Datos de prueba (productos, usuarios, pedidos)

### Paso 3: Configurar la conexión

Verificar el archivo `ecommerce_E5/src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="ITSON"/>
```

### Paso 4: Importar en NetBeans

1. Abrir NetBeans
2. **File** → **Open Project**
3. Seleccionar la carpeta `ecommerce_E5` (la que contiene el `pom.xml`)
4. Esperar a que Maven descargue las dependencias

### Paso 5: Clean and Build y Ejecutar

1. Clic derecho en el proyecto → **Clean and Build**
2. Clic derecho en el proyecto → **Run**
3. Acceder a: `http://localhost:8080/ecommerce_E5/`

---

## Estructura del Proyecto

```
ecommerce_E5/src/main/java/com/mycompany/ecommerce_e5/
├── dominio/           # Entidades JPA (Usuario, Producto, Pedido, etc.)
│   └── enums/         # Enumeraciones (RolUsuario, EstadoPedido, etc.)
├── dao/               # Data Access Objects (acceso a BD)
├── bo/                # Business Objects (lógica de negocio)
├── servlets/          # Controladores
│   ├── admin/         # Panel de administración
│   └── cliente/       # Tienda para clientes
├── rest/              # API REST (Jersey/JAX-RS)
│   └── dto/           # Data Transfer Objects
├── filters/           # Filtros de autenticación
├── excepciones/       # Excepciones personalizadas
└── util/              # Utilidades (JPAUtil, ValidationUtil, etc.)
```

---

## Usuarios de prueba

Los usuarios ya vienen en el script de la base de datos:

**Administrador:**
- Correo: `admin@mitienda.com`
- Contraseña: `admin123`

**Clientes:**
- Correo: `maria@email.com` / Contraseña: `admin123`
- Correo: `ana@email.com` / Contraseña: `admin123`
- Correo: `laura@email.com` / Contraseña: `admin123`

---

## Rutas Principales

### Tienda (Clientes)

`/` - Página de inicio
`/cliente/productos` - Catálogo de productos
`/cliente/productos?accion=detalle&id=1` - Detalle de producto
`/cliente/carrito` - Carrito de compras
`/cliente/checkout` - Proceso de pago
`/cliente/cuenta` - Mi cuenta
`/login` - Iniciar sesión
`/register` - Registro de usuario

### Panel de Administración

`/admin/login` - Login de administrador
`/admin/dashboard` - Panel principal
`/admin/producto` - Gestión de productos
`/admin/pedidos` - Gestión de pedidos
`/admin/resenas` - Moderación de reseñas

---

## API REST

La API REST está disponible en `/api/*` y utiliza JSON para las peticiones y respuestas.

### Formato de Respuesta

Todas las respuestas siguen este formato:

```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { ... }
}
```

En caso de error:

```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null
}
```

### Endpoints de Autenticación (`/api/auth`)

`POST` - `/api/auth/login` - Iniciar sesión - `{"correo": "...", "contrasena": "..."}` 
`POST` - `/api/auth/logout` - Cerrar sesión
`GET` - `/api/auth/status` - Verificar autenticación

### Endpoints de Productos (`/api/productos`)

`GET` - `/api/productos` - Listar productos - `?q=busqueda`, `?categoria=1`, `?page=1&size=12`
`GET` - `/api/productos/{id}` - Detalle de producto
`GET` - `/api/productos/{id}/resenas` - Reseñas del producto
`POST` - `/api/productos/{id}/resenas` - Agregar reseña - `{"calificacion": 5, "comentario": "..."}`

### Endpoints de Categorías (`/api/categorias`)

`GET` - `/api/categorias` - Listar todas las categorías
`GET` - `/api/categorias/{id}` - Obtener categoría por ID

### Endpoints de Carrito (`/api/carrito`)

`GET` - `/api/carrito` - Ver carrito
`POST` - `/api/carrito` - Agregar producto - `{"productoId": 1, "cantidad": 2}`
`PUT` - `/api/carrito/{productoId}` - Actualizar cantidad - `{"cantidad": 3}`
`DELETE` - `/api/carrito/{productoId}` - Eliminar producto 
`DELETE` - `/api/carrito` - Vaciar carrito

### Endpoints de Pedidos (`/api/pedidos`)

`GET` - `/api/pedidos` - Mis pedidos
`GET` - `/api/pedidos/{id}` - Detalle de pedido
`POST` - `/api/pedidos` - Crear pedido (checkout) - `{"direccionId": 1, "metodoPago": "TARJETA"}`
`POST` - `/api/pedidos/{id}/cancelar` - Cancelar pedido

**Métodos de pago válidos:** `TARJETA`, `PAYPAL`

### Endpoints de Perfil (`/api/perfil`)

`GET` - `/api/perfil` - Obtener perfil
`PUT` - `/api/perfil` - Actualizar perfil - `{"nombre": "...", "telefono": "..."}`
`GET` - `/api/perfil/direcciones` - Mis direcciones
`POST` - `/api/perfil/direcciones` - Agregar dirección - `{"calle": "...", "ciudad": "...", "estado": "...", "codigoPostal": "..."}`

---

## Solución de Problemas

### Error de conexión a MySQL

**Síntoma:** `Cannot create PoolableConnectionFactory` o `Communications link failure`

**Soluciones:**
1. Verificar que MySQL esté corriendo.
2. Verificar credenciales en `persistence.xml`
3. Verificar que la base de datos exista:
   ```sql
   SHOW DATABASES LIKE 'ecommerce_db';
   ```

### Puerto 8080 ocupado

**Síntoma:** `Address already in use: bind`

**Solución:**
1. Cambiar el puerto en Tomcat (server.xml)

### Imágenes no cargan

**Síntoma:** Productos sin imagen o imagen rota

**Soluciones:**
1. Verificar que exista la carpeta `webapp/imgs/`
2. Verificar la ruta en la base de datos (debe ser relativa: `imgs/vestidos/vestido.png`)
3. Limpiar caché del navegador

### Error al eliminar producto

**Síntoma:** `Cannot delete or update a parent row: a foreign key constraint fails`

**Causa:** El producto tiene pedidos, reseñas o está en carritos activos.

**Solución:** El sistema muestra un mensaje amigable. Para eliminar, primero hay que:
1. Eliminar las reseñas asociadas
2. Esperar que no haya pedidos pendientes con ese producto

---

## Base de Datos

### Tablas Principales

`usuario` - Clientes y administradores 
`producto` - Artículos de la tienda 
`categoria` - Clasificación de productos 
`pedido` - Órdenes de compra 
`detalle_pedido` - Items de cada pedido 
`direccion` - Direcciones de envío 
`pago` - Información de pagos 
`resena` - Opiniones de clientes 


---

## Funcionalidades

### Tienda (Cliente)
- Ver catálogo de productos
- Filtrar por categoría
- Buscar productos por nombre
- Ver detalle de producto con reseñas
- Carrito de compras (guardado en sesión)
- Proceso de checkout
- Registro e inicio de sesión
- Ver y editar datos de la cuenta
- Agregar reseñas a productos comprados

### Panel Admin
- Dashboard con estadísticas
- CRUD completo de productos
- Ver y gestionar pedidos (cambiar estado)
- Moderar/eliminar reseñas

---

## Notas Técnicas

- El carrito se guarda en la sesión HTTP del usuario
- Las contraseñas se almacenan hasheadas con BCrypt
- Envío gratis a partir de $1,500 MXN
- Máximo 10 unidades por producto en el carrito
- Un usuario solo puede dejar una reseña por producto
- Los filtros de autenticación protegen las rutas `/admin/*` y `/cliente/*`

---

## Diagramas

- **Diagrama de Clases:** `Diagrama/Diagrama de clases E5.pdf`
- **Modelo Entidad-Relación:** `MER/MER.png`
- **Storyboard:** `Ecommerce Storyboard/`

---
