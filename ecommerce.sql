CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

ALTER USER 'root'@'localhost' IDENTIFIED BY 'ITSON';

-- Tabla: Categoria
CREATE TABLE Categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255)

-- Tabla: Usuario
CREATE TABLE Usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    correo VARCHAR(255) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    rol ENUM('CLIENTE', 'ADMINISTRADOR') NOT NULL
);

-- Tabla: Pago
CREATE TABLE Pago (
    id INT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(10, 2) NOT NULL,
    fecha DATE,
    metodo ENUM('TARJETA', 'TRANSFERENCIA', 'CONTRA_ENTREGA'),
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO')
);

-- Tabla: Direccion
CREATE TABLE Direccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(255),
    ciudad VARCHAR(255),
    estado VARCHAR(255),
    codigoPostal VARCHAR(20),
    usuario_id INT,
    CONSTRAINT fk_direccion_usuario FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

-- Tabla: Producto 
CREATE TABLE Producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10, 2) NOT NULL CHECK (precio > 0),
    imagenUrl VARCHAR(255),
    existencias INT NOT NULL CHECK (existencias >= 0),
    talla VARCHAR(50),
    color VARCHAR(50),
    categoria_id INT NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES Categoria(id)
);

-- Tabla: Pedido
CREATE TABLE Pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numeroPedido VARCHAR(255),
    fecha DATE,
    total DECIMAL(10, 2),
    estado ENUM('PENDIENTE', 'ENVIADO', 'ENTREGADO', 'CANCELADO'),
    usuario_id INT,
    direccion_envio_id INT,
    pago_id INT UNIQUE,
    
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (direccion_envio_id) REFERENCES Direccion(id),
    CONSTRAINT fk_pedido_pago FOREIGN KEY (pago_id) REFERENCES Pago(id)
);

-- Tabla: DetallePedido
CREATE TABLE DetallePedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precioUnitario DECIMAL(10, 2),
    pedido_id INT,
    producto_id INT,
    
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES Pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES Producto(id)
);

-- Tabla: Resena
CREATE TABLE Resena (
    id INT AUTO_INCREMENT PRIMARY KEY,
    calificacion INT CHECK (calificacion BETWEEN 1 AND 5),
    comentario TEXT,
    fecha DATE,
    usuario_id INT,
    producto_id INT,
    
    CONSTRAINT fk_resena_usuario FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    CONSTRAINT fk_resena_producto FOREIGN KEY (producto_id) REFERENCES Producto(id)
);