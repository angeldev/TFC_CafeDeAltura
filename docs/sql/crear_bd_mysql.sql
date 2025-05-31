-- Script  para la base de datos TFC_CafeDeAltura
-- Autor: Lola Fernández Fuentes / Adaptado por IA profesional

-- 1. Crear base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS tfc_cafedealtura CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tfc_cafedealtura;

-- 2. Eliminar tablas en orden correcto (por claves foráneas)
DROP TABLE IF EXISTS linea_de_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS cafe;
DROP TABLE IF EXISTS cliente;

-- 3. Crear tabla de clientes
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    direccion VARCHAR(200),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    UNIQUE(email)
);

-- 4. Crear tabla de cafés
CREATE TABLE cafe (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(8,2) NOT NULL,
    origen VARCHAR(80) NOT NULL,
    intensidad INT NOT NULL CHECK (intensidad >= 1 AND intensidad <= 10),
    stock INT NOT NULL
);

-- 5. Crear tabla de pedidos
CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2),
    estado VARCHAR(30),
    comentario VARCHAR(255),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- 6. Crear tabla de líneas de pedido
CREATE TABLE linea_de_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    cafe_id BIGINT NOT NULL,
    precio_unitario DECIMAL(8,2) NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (cafe_id) REFERENCES cafe(id)
);

-- 7. Índices útiles
CREATE INDEX idx_cafe_origen ON cafe(origen);
CREATE INDEX idx_cafe_nombre ON cafe(nombre);
CREATE INDEX idx_pedido_cliente ON pedido(cliente_id);
CREATE INDEX idx_cliente_email ON cliente(email);
CREATE INDEX idx_pedido_fecha_creacion ON pedido(fecha_creacion);

-- 8. Datos de ejemplo

-- Clientes
INSERT INTO cliente (nombre, email, telefono, direccion, fecha_registro, activo) VALUES
('Marina López', 'marina.lopez@gmail.com', '600123456', 'Calle Mayor 12, Madrid', '2024-05-01 10:00:00', TRUE),
('Javier Ortega', 'javier.ortega@empresa.com', '611987654', 'Avda. Andalucía 45, Sevilla', '2024-05-02 11:30:00', TRUE),
('Sofía Müller', 'sofia.muller@correo.de', '699112233', 'Plaza Europa 3, Barcelona', '2024-05-03 09:15:00', TRUE);

-- Cafés
INSERT INTO cafe (nombre, descripcion, precio, origen, intensidad, stock) VALUES
('Altura Colombia', 'Café de altura con notas a chocolate y frutos rojos. Cosechado en Nariño.', 10.50, 'Colombia', 7, 50),
('Brasil Cerrado', 'Suave, achocolatado, con baja acidez. Ideal para espresso.', 8.90, 'Brasil', 5, 80),
('Etiopía Yirgacheffe', 'Aromático, floral, con toques cítricos. Tueste medio.', 12.20, 'Etiopía', 8, 30),
('Guatemala Antigua', 'Cuerpo medio, notas a cacao y nuez. Sostenible.', 11.00, 'Guatemala', 6, 40),
('Sumatra Mandheling', 'Intenso, terroso, con final largo. Perfecto para filtro.', 13.80, 'Indonesia', 9, 25),
('Descafeinado Swiss Water', 'Descafeinado natural, sabor equilibrado y sin químicos.', 9.50, 'Perú', 4, 20);

-- Pedidos
INSERT INTO pedido (cliente_id, fecha_creacion, total, estado, comentario) VALUES
(1, '2024-05-10 12:00:00', 29.60, 'PENDIENTE', 'Entrega en horario de mañana'),
(2, '2024-05-11 15:30:00', 21.80, 'ENVIADO', 'Sin azúcar, por favor'),
(3, '2024-05-12 09:45:00', 13.80, 'PENDIENTE', NULL);

-- Líneas de pedido
INSERT INTO linea_de_pedido (pedido_id, cafe_id, precio_unitario, cantidad, subtotal) VALUES
(1, 1, 10.50, 2, 21.00),
(1, 3, 12.20, 1, 12.20),
(2, 2, 8.90, 2, 17.80),
(2, 6, 9.50, 1, 9.50),
(3, 5, 13.80, 1, 13.80);

-- (5, 6, 9.50, 0, 0.00); -- Ejemplo de línea con cantidad 0 (para testear validaciones)
