-- ecommerce_db.sql
-- Script de la base de datos para la tienda de ropa E5
--
-- Autores: Alberto Jimenez Garcia (252595)
--          Rene Ezequiel Figueroa Lopez (228691)
--          Freddy Ali Castro Roman (252191)
--
-- Materia: Aplicaciones Web

-- Borramos la BD si ya existe y la creamos de nuevo
DROP DATABASE IF EXISTS ecommerce_db;
CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_db;

-- Tabla categoria - guarda los tipos de ropa (vestidos, blusas, etc)
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,

    INDEX idx_categoria_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla usuario - clientes y administradores
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    telefono VARCHAR(15),
    rol ENUM('CLIENTE', 'ADMINISTRADOR') NOT NULL DEFAULT 'CLIENTE',

    INDEX idx_usuario_correo (correo),
    INDEX idx_usuario_rol (rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla direccion - para guardar a donde se envian los pedidos
CREATE TABLE direccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(200) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    estado VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    usuario_id INT NOT NULL,

    INDEX idx_direccion_usuario (usuario_id),

    CONSTRAINT fk_direccion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla producto - todos los articulos de la tienda
CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10,2) NOT NULL,
    imagen_url VARCHAR(255),
    existencias INT NOT NULL DEFAULT 0,
    talla VARCHAR(50),
    color VARCHAR(50),
    categoria_id INT NOT NULL,

    INDEX idx_producto_nombre (nombre),
    INDEX idx_producto_categoria (categoria_id),
    INDEX idx_producto_precio (precio),

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_existencias CHECK (existencias >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla pago - info de los pagos (tarjeta o paypal)
CREATE TABLE pago (
    id INT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(10,2) NOT NULL,
    fecha DATETIME NOT NULL,
    metodo ENUM('TARJETA', 'PAYPAL') NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',

    INDEX idx_pago_estado (estado),
    INDEX idx_pago_fecha (fecha),

    CONSTRAINT chk_pago_monto CHECK (monto >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla pedido - las ordenes de compra de los clientes
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(50) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('PENDIENTE', 'ENVIADO', 'ENTREGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    usuario_id INT NOT NULL,
    direccion_id INT NOT NULL,
    pago_id INT,

    INDEX idx_pedido_numero (numero_pedido),
    INDEX idx_pedido_usuario (usuario_id),
    INDEX idx_pedido_estado (estado),
    INDEX idx_pedido_fecha (fecha),

    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_pedido_direccion
        FOREIGN KEY (direccion_id) REFERENCES direccion(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_pedido_pago
        FOREIGN KEY (pago_id) REFERENCES pago(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT chk_pedido_total CHECK (total >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla detalle_pedido - los productos que tiene cada pedido
CREATE TABLE detalle_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,

    INDEX idx_detalle_pedido (pedido_id),
    INDEX idx_detalle_producto (producto_id),

    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_precio CHECK (precio_unitario >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla resena - opiniones de los clientes sobre los productos
CREATE TABLE resena (
    id INT AUTO_INCREMENT PRIMARY KEY,
    calificacion INT NOT NULL,
    comentario VARCHAR(1000),
    fecha DATE NOT NULL,
    usuario_id INT NOT NULL,
    producto_id INT NOT NULL,

    INDEX idx_resena_producto (producto_id),
    INDEX idx_resena_usuario (usuario_id),
    INDEX idx_resena_calificacion (calificacion),

    CONSTRAINT fk_resena_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_resena_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT chk_resena_calificacion CHECK (calificacion >= 1 AND calificacion <= 5),

    -- Un usuario solo puede dejar una reseña por producto
    UNIQUE KEY uk_resena_usuario_producto (usuario_id, producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- DATOS DE PRUEBA
-- Insertamos datos para probar que todo funcione bien

-- Categorias de ropa
INSERT INTO categoria (nombre) VALUES
    ('Vestidos'),
    ('Blusas'),
    ('Pantalones'),
    ('Faldas'),
    ('Conjuntos'),
    ('Blazers'),
    ('Tops'),
    ('Accesorios');

-- Usuario admin (contrasena: admin123 - esta hasheada con BCrypt)
INSERT INTO usuario (nombre, correo, contrasena, telefono, rol) VALUES
    ('Administrador', 'admin@mitienda.com', '$2a$10$xXZAhjkijc3K./JbrZJnvOVqRMT2PAnXQCLmbo2CjH5pxj.QAnRE.', '6441234567', 'ADMINISTRADOR');

-- Clientes de prueba (misma contrasena: admin123)
INSERT INTO usuario (nombre, correo, contrasena, telefono, rol) VALUES
    ('María García', 'maria@email.com', '$2a$10$xXZAhjkijc3K./JbrZJnvOVqRMT2PAnXQCLmbo2CjH5pxj.QAnRE.', '6449876543', 'CLIENTE'),
    ('Ana López', 'ana@email.com', '$2a$10$xXZAhjkijc3K./JbrZJnvOVqRMT2PAnXQCLmbo2CjH5pxj.QAnRE.', '6441112233', 'CLIENTE'),
    ('Laura Martínez', 'laura@email.com', '$2a$10$xXZAhjkijc3K./JbrZJnvOVqRMT2PAnXQCLmbo2CjH5pxj.QAnRE.', '6444445566', 'CLIENTE');

-- Direcciones de envio
INSERT INTO direccion (calle, ciudad, estado, codigo_postal, usuario_id) VALUES
    ('Av. Principal #123', 'Ciudad Obregón', 'Sonora', '85000', 2),
    ('Calle Reforma #456', 'Ciudad Obregón', 'Sonora', '85010', 2),
    ('Blvd. Las Torres #789', 'Hermosillo', 'Sonora', '83000', 3),
    ('Calle Juárez #321', 'Navojoa', 'Sonora', '85800', 4);

-- Productos de la tienda (ropa y accesorios)
INSERT INTO producto (nombre, descripcion, precio, imagen_url, existencias, talla, color, categoria_id) VALUES
    -- Vestidos (categoria_id = 1)
    ('Vestido Azul Largo', 'Elegante vestido largo en tono azul, perfecto para eventos formales y ocasiones especiales.', 899.00, 'imgs/vestidos/vestidoAzulLargo.png', 15, 'M', 'Azul', 1),
    ('Vestido Negro Corto', 'Vestido negro corto y versátil, ideal para salidas nocturnas o cenas elegantes.', 750.00, 'imgs/vestidos/vestidoNegroCorto.png', 20, 'S', 'Negro', 1),

    -- Blusas (categoria_id = 2)
    ('Blusa Cruzada Estampado Gris', 'Blusa con diseño cruzado y estampado en tonos grises, elegante y moderna.', 420.00, 'imgs/blusas/blusaCruzadaEstampadoGris.png', 25, 'M', 'Gris', 2),
    ('Blusa Dorada Metálica', 'Blusa con acabado metálico dorado, perfecta para fiestas y eventos especiales.', 550.00, 'imgs/blusas/blusaDoradaMetalica.png', 18, 'S', 'Dorado', 2),
    ('Blusa Rayas Azules', 'Blusa fresca con patrón de rayas azules, ideal para el día a día.', 350.00, 'imgs/blusas/blusaRayasAzules.png', 30, 'L', 'Azul/Blanco', 2),

    -- Pantalones (categoria_id = 3)
    ('Pantalón Estampado Beige Negro', 'Pantalón con estampado elegante en tonos beige y negro, perfecto para oficina.', 680.00, 'imgs/pantalones/pantalonEstampadoBeigeNegro.png', 22, 'M', 'Beige/Negro', 3),
    ('Pantalón Negro Fluido', 'Pantalón negro de tela fluida, cómodo y elegante para cualquier ocasión.', 520.00, 'imgs/pantalones/pantalonNegroFluido.png', 35, '28', 'Negro', 3),
    ('Pantalón Negro Sastre Ancho', 'Pantalón de sastre negro con corte ancho, estilo profesional y sofisticado.', 750.00, 'imgs/pantalones/pantalonNegroSastreAncho.png', 20, '30', 'Negro', 3),

    -- Faldas (categoria_id = 4)
    ('Falda Beige Midi con Abertura', 'Falda midi en tono beige con abertura lateral, elegante y femenina.', 480.00, 'imgs/faldas/faldaBeigeMidiAbertura.png', 25, 'M', 'Beige', 4),
    ('Falda Negra Corta', 'Falda negra corta, básico versátil para combinar con cualquier outfit.', 350.00, 'imgs/faldas/faldaNegraCorta.png', 40, 'S', 'Negro', 4),
    ('Falda Plisada Estampado Café', 'Falda plisada con elegante estampado en tonos café, perfecta para looks sofisticados.', 420.00, 'imgs/faldas/faldaPlisadaEstampadoCafe.png', 30, 'M', 'Café', 4),

    -- Conjuntos (categoria_id = 5)
    ('Conjunto Azul con Volantes', 'Conjunto azul con detalles de volantes, perfecto para ocasiones especiales.', 1350.00, 'imgs/conjuntos/conjuntoAzulVolantes.png', 12, 'M', 'Azul', 5),
    ('Conjunto Estampado Beige Negro', 'Conjunto coordinado con estampado en beige y negro, elegante y moderno.', 1200.00, 'imgs/conjuntos/conjuntoEstampadoBeigeNegro.png', 15, 'S', 'Beige/Negro', 5),
    ('Conjunto Negro Casual', 'Conjunto negro casual de dos piezas, cómodo y estiloso.', 980.00, 'imgs/conjuntos/conjuntoNegroCasual.png', 20, 'M', 'Negro', 5),
    ('Conjunto Rayas Naranja', 'Conjunto con patrón de rayas en tonos naranja, vibrante y moderno.', 1100.00, 'imgs/conjuntos/conjuntoRayasNaranja.png', 18, 'L', 'Naranja', 5),
    ('Conjunto Rojo con Pantalón', 'Conjunto rojo de blusa y pantalón, ideal para eventos y fiestas.', 1450.00, 'imgs/conjuntos/conjuntoRojoPantalon.png', 10, 'M', 'Rojo', 5),

    -- Blazers (categoria_id = 6)
    ('Blazer Azul Rayas', 'Blazer azul con patrón de rayas sutiles, perfecto para oficina o eventos.', 950.00, 'imgs/blazers/blazerAzulRayas.png', 15, 'M', 'Azul', 6),
    ('Blazer Gris Rayas', 'Blazer gris con rayas elegantes, estilo profesional y sofisticado.', 920.00, 'imgs/blazers/blazerGrisRayas.png', 18, 'S', 'Gris', 6),
    ('Blazer Negro Casual', 'Blazer negro de corte casual, versátil para cualquier ocasión.', 850.00, 'imgs/blazers/blazerNegroCasual.png', 22, 'M', 'Negro', 6),

    -- Tops (categoria_id = 7)
    ('Top Beige Cuello Alto', 'Top beige con cuello alto, elegante y minimalista.', 280.00, 'imgs/tops/topBeigeCuelloAlto.png', 35, 'S', 'Beige', 7),
    ('Top Negro Escote Drapeado', 'Top negro con escote drapeado, sofisticado y femenino.', 320.00, 'imgs/tops/topNegroEscoteDrapeado.png', 30, 'M', 'Negro', 7),

    -- Accesorios (categoria_id = 8)
    ('Bolsa Beige Asa Trenzada', 'Bolsa en tono beige con asa trenzada, elegante y práctica.', 650.00, 'imgs/accesorios/bolsaBeigeAsaTrenzada.png', 20, 'Única', 'Beige', 8),
    ('Bolsa Rosa Estructurada', 'Bolsa rosa con diseño estructurado, perfecta para ocasiones especiales.', 720.00, 'imgs/accesorios/bolsaRosaEstructurada.png', 15, 'Única', 'Rosa', 8),
    ('Bolsa Vino Grande', 'Bolsa grande en color vino, espaciosa y elegante para el día a día.', 580.00, 'imgs/accesorios/bolsaVinoGrande.png', 25, 'Única', 'Vino', 8);

-- Pagos de ejemplo
INSERT INTO pago (monto, fecha, metodo, estado) VALUES
    (1798.00, '2024-11-15 10:30:00', 'TARJETA', 'APROBADO'),
    (650.00, '2024-11-18 14:20:00', 'PAYPAL', 'APROBADO'),
    (1200.00, '2024-11-20 09:15:00', 'TARJETA', 'PENDIENTE');

-- Pedidos de ejemplo
INSERT INTO pedido (numero_pedido, fecha, total, estado, usuario_id, direccion_id, pago_id) VALUES
    ('PED-2024-001', '2024-11-15 10:30:00', 1798.00, 'ENTREGADO', 2, 1, 1),
    ('PED-2024-002', '2024-11-18 14:20:00', 650.00, 'ENVIADO', 3, 3, 2),
    ('PED-2024-003', '2024-11-20 09:15:00', 1200.00, 'PENDIENTE', 4, 4, 3);

-- Detalles de cada pedido (que productos compraron)
INSERT INTO detalle_pedido (cantidad, precio_unitario, pedido_id, producto_id) VALUES
    -- Pedido 1: Vestido Azul Largo + Blusa Dorada Metálica
    (1, 899.00, 1, 1),
    (2, 550.00, 1, 4),

    -- Pedido 2: Bolsa Beige Asa Trenzada
    (1, 650.00, 2, 21),

    -- Pedido 3: Conjunto Estampado Beige Negro
    (1, 1200.00, 3, 12);

-- Resenas de los clientes
INSERT INTO resena (calificacion, comentario, fecha, usuario_id, producto_id) VALUES
    (5, '¡Excelente vestido! La tela es muy cómoda y el color azul es precioso. Muy recomendado.', '2024-11-16', 2, 1),
    (4, 'La blusa dorada es hermosa, perfecta para fiestas. Tardó un poco en llegar pero vale la pena.', '2024-11-17', 2, 4),
    (5, 'La bolsa es muy práctica y elegante, el asa trenzada le da un toque especial.', '2024-11-19', 3, 21),
    (4, 'El conjunto es muy bonito, solo que la talla viene un poco grande.', '2024-11-21', 4, 12),
    (5, 'El blazer gris es hermoso, muy elegante y de excelente calidad.', '2024-11-22', 3, 17);


-- VISTAS
-- Las usamos para consultas mas faciles desde Java

-- Vista que muestra productos con su categoria y calificacion
CREATE VIEW v_productos_detalle AS
SELECT
    p.id,
    p.nombre,
    p.descripcion,
    p.precio,
    p.imagen_url,
    p.existencias,
    p.talla,
    p.color,
    c.nombre AS categoria,
    COALESCE(AVG(r.calificacion), 0) AS calificacion_promedio,
    COUNT(r.id) AS total_resenas
FROM producto p
INNER JOIN categoria c ON p.categoria_id = c.id
LEFT JOIN resena r ON p.id = r.producto_id
GROUP BY p.id;

-- Vista para ver los pedidos con toda su info (cliente, direccion, pago)
CREATE VIEW v_pedidos_detalle AS
SELECT
    p.id,
    p.numero_pedido,
    p.fecha,
    p.total,
    p.estado,
    u.nombre AS cliente,
    u.correo AS cliente_correo,
    CONCAT(d.calle, ', ', d.ciudad, ', ', d.estado, ' ', d.codigo_postal) AS direccion_envio,
    pa.metodo AS metodo_pago,
    pa.estado AS estado_pago
FROM pedido p
INNER JOIN usuario u ON p.usuario_id = u.id
INNER JOIN direccion d ON p.direccion_id = d.id
LEFT JOIN pago pa ON p.pago_id = pa.id;

-- Vista para el dashboard - ventas por categoria
CREATE VIEW v_ventas_categoria AS
SELECT
    c.nombre AS categoria,
    COUNT(DISTINCT dp.pedido_id) AS total_pedidos,
    SUM(dp.cantidad) AS productos_vendidos,
    SUM(dp.cantidad * dp.precio_unitario) AS ingresos_totales
FROM categoria c
INNER JOIN producto p ON c.id = p.categoria_id
INNER JOIN detalle_pedido dp ON p.id = dp.producto_id
INNER JOIN pedido pe ON dp.pedido_id = pe.id
WHERE pe.estado != 'CANCELADO'
GROUP BY c.id
ORDER BY ingresos_totales DESC;


-- PROCEDIMIENTOS ALMACENADOS

DELIMITER //

-- Genera un numero de pedido unico (PED-2024-001, PED-2024-002, etc)
CREATE PROCEDURE sp_generar_numero_pedido(OUT nuevo_numero VARCHAR(50))
BEGIN
    DECLARE anio VARCHAR(4);
    DECLARE siguiente_num INT;

    SET anio = YEAR(CURDATE());

    SELECT COALESCE(MAX(CAST(SUBSTRING(numero_pedido, 10) AS UNSIGNED)), 0) + 1
    INTO siguiente_num
    FROM pedido
    WHERE numero_pedido LIKE CONCAT('PED-', anio, '-%');

    SET nuevo_numero = CONCAT('PED-', anio, '-', LPAD(siguiente_num, 3, '0'));
END //

-- Obtiene los productos mas vendidos (para el home)
CREATE PROCEDURE sp_productos_mas_vendidos(IN limite INT)
BEGIN
    SELECT
        p.id,
        p.nombre,
        p.precio,
        p.imagen_url,
        c.nombre AS categoria,
        SUM(dp.cantidad) AS total_vendido
    FROM producto p
    INNER JOIN categoria c ON p.categoria_id = c.id
    INNER JOIN detalle_pedido dp ON p.id = dp.producto_id
    INNER JOIN pedido pe ON dp.pedido_id = pe.id
    WHERE pe.estado != 'CANCELADO'
    GROUP BY p.id
    ORDER BY total_vendido DESC
    LIMIT limite;
END //

-- Reduce el stock cuando alguien compra un producto
CREATE PROCEDURE sp_reducir_stock(IN p_producto_id INT, IN p_cantidad INT)
BEGIN
    DECLARE stock_actual INT;

    SELECT existencias INTO stock_actual
    FROM producto WHERE id = p_producto_id;

    IF stock_actual >= p_cantidad THEN
        UPDATE producto
        SET existencias = existencias - p_cantidad
        WHERE id = p_producto_id;
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para el producto';
    END IF;
END //

DELIMITER ;


-- TRIGGERS
-- Se ejecutan automaticamente cuando pasa algo en las tablas

DELIMITER //

-- Valida que la calificacion este entre 1 y 5
CREATE TRIGGER tr_validar_calificacion
BEFORE INSERT ON resena
FOR EACH ROW
BEGIN
    IF NEW.calificacion < 1 OR NEW.calificacion > 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La calificación debe estar entre 1 y 5';
    END IF;
END //

-- Si no ponen fecha en la resena, le pone la fecha de hoy
CREATE TRIGGER tr_fecha_resena
BEFORE INSERT ON resena
FOR EACH ROW
BEGIN
    IF NEW.fecha IS NULL THEN
        SET NEW.fecha = CURDATE();
    END IF;
END //

DELIMITER ;


-- VERIFICACION - para checar que se insertaron bien los datos

SELECT '--- Resumen de datos insertados ---' AS '';
SELECT CONCAT('Categorías: ', COUNT(*)) AS resultado FROM categoria
UNION ALL
SELECT CONCAT('Usuarios: ', COUNT(*)) FROM usuario
UNION ALL
SELECT CONCAT('Direcciones: ', COUNT(*)) FROM direccion
UNION ALL
SELECT CONCAT('Productos: ', COUNT(*)) FROM producto
UNION ALL
SELECT CONCAT('Pedidos: ', COUNT(*)) FROM pedido
UNION ALL
SELECT CONCAT('Detalles Pedido: ', COUNT(*)) FROM detalle_pedido
UNION ALL
SELECT CONCAT('Pagos: ', COUNT(*)) FROM pago
UNION ALL
SELECT CONCAT('Reseñas: ', COUNT(*)) FROM resena;

SELECT 'Base de datos creada' AS '';
