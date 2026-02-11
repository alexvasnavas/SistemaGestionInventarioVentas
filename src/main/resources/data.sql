-- ============================================
-- SISTEMA DE INVENTARIO Y VENTAS
-- DATOS INICIALES PARA PRUEBAS
-- ============================================

USE inventario_db;

-- ============================================
-- 1. LIMPIAR TABLAS (ORDEN CORRECTO POR FOREIGN KEYS)
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE detalle_ventas;
TRUNCATE TABLE ventas;
TRUNCATE TABLE productos;
TRUNCATE TABLE categorias;
TRUNCATE TABLE usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 2. RESETEAR AUTO_INCREMENT
-- ============================================
ALTER TABLE categorias AUTO_INCREMENT = 1;
ALTER TABLE productos AUTO_INCREMENT = 1;
ALTER TABLE ventas AUTO_INCREMENT = 1;
ALTER TABLE detalle_ventas AUTO_INCREMENT = 1;
ALTER TABLE usuarios AUTO_INCREMENT = 1;

-- ============================================
-- 3. CATEGORÍAS
-- ============================================
INSERT INTO categorias (nombre, descripcion) VALUES
                                                 ('Electrónica', 'Dispositivos electrónicos, computadoras, teléfonos y accesorios'),
                                                 ('Ropa', 'Prendas de vestir para hombres, mujeres y niños'),
                                                 ('Hogar', 'Artículos para el hogar, muebles y decoración'),
                                                 ('Deportes', 'Equipamiento deportivo, ropa y accesorios'),
                                                 ('Libros', 'Libros de todos los géneros y editoriales'),
                                                 ('Juguetes', 'Juguetes para todas las edades'),
                                                 ('Alimentos', 'Productos alimenticios y bebidas'),
                                                 ('Belleza', 'Productos de cuidado personal y cosméticos');

-- ============================================
-- 4. PRODUCTOS
-- ============================================
INSERT INTO productos (sku, nombre, precio, stock_actual, categoria_id, version) VALUES
-- Electrónica (categoria_id = 1)
('SKU-001', 'Smartphone Samsung Galaxy S23', 999.99, 50, 1, 0),
('SKU-002', 'Laptop Dell XPS 13', 1299.99, 25, 1, 0),
('SKU-003', 'iPhone 15 Pro', 1199.99, 30, 1, 0),
('SKU-004', 'Tablet iPad Air', 599.99, 40, 1, 0),
('SKU-005', 'Auriculares Sony WH-1000XM4', 349.99, 100, 1, 0),
('SKU-006', 'Monitor LG 27" 4K', 399.99, 35, 1, 0),
('SKU-007', 'Teclado Mecánico Logitech', 89.99, 80, 1, 0),

-- Ropa (categoria_id = 2)
('SKU-008', 'Camiseta Algodón Premium', 24.99, 200, 2, 0),
('SKU-009', 'Jeans Slim Fit Azul', 59.99, 150, 2, 0),
('SKU-010', 'Chaqueta Impermeable', 89.99, 75, 2, 0),
('SKU-011', 'Vestido Casual', 49.99, 60, 2, 0),
('SKU-012', 'Zapatos Deportivos', 79.99, 90, 2, 0),
('SKU-013', 'Bufanda de Lana', 19.99, 120, 2, 0),

-- Hogar (categoria_id = 3)
('SKU-014', 'Sofá 3 Plazas Moderno', 499.99, 15, 3, 0),
('SKU-015', 'Mesa de Centro Roble', 199.99, 40, 3, 0),
('SKU-016', 'Lámpara LED Moderna', 49.99, 120, 3, 0),
('SKU-017', 'Juego de Sábanas', 39.99, 200, 3, 0),
('SKU-018', 'Cocina Eléctrica', 299.99, 25, 3, 0),
('SKU-019', 'Set de Ollas Antiadherentes', 89.99, 50, 3, 0),

-- Deportes (categoria_id = 4)
('SKU-020', 'Balón de Fútbol Profesional', 29.99, 80, 4, 0),
('SKU-021', 'Raqueta de Tenis', 89.99, 45, 4, 0),
('SKU-022', 'Zapatillas Running', 79.99, 60, 4, 0),
('SKU-023', 'Pesas 5kg', 24.99, 150, 4, 0),
('SKU-024', 'Bicicleta Estática', 299.99, 20, 4, 0),

-- Libros (categoria_id = 5)
('SKU-025', 'Cien Años de Soledad', 14.99, 150, 5, 0),
('SKU-026', 'El Principito', 9.99, 200, 5, 0),
('SKU-027', 'Harry Potter y la Piedra Filosofal', 19.99, 90, 5, 0),
('SKU-028', '1984 - George Orwell', 12.99, 120, 5, 0),
('SKU-029', 'El Alquimista', 11.99, 100, 5, 0),

-- Juguetes (categoria_id = 6)
('SKU-030', 'LEGO Technic', 89.99, 40, 6, 0),
('SKU-031', 'Muñeca Barbie', 24.99, 150, 6, 0),
('SKU-032', 'Hot Wheels Pack', 14.99, 300, 6, 0),

-- Alimentos (categoria_id = 7)
('SKU-033', 'Café Premium 500g', 12.99, 200, 7, 0),
('SKU-034', 'Chocolate Negro 70%', 4.99, 500, 7, 0),
('SKU-035', 'Aceite de Oliva Extra Virgen', 8.99, 150, 7, 0),

-- Belleza (categoria_id = 8)
('SKU-036', 'Perfume Hombre 100ml', 59.99, 80, 8, 0),
('SKU-037', 'Crema Hidratante Facial', 24.99, 120, 8, 0),
('SKU-038', 'Set de Maquillaje', 39.99, 60, 8, 0);

-- ============================================
-- 5. USUARIOS (CONTRASEÑAS ENCRIPTADAS CON BCRYPT)
-- CONTRASEÑA PARA TODOS: "123456"
-- ============================================
INSERT INTO usuarios (email, password, nombre, apellido, rol, activo, fecha_registro) VALUES
                                                                                          ('admin@inventory.com', '$2a$10$NkM3Y8QqKqKqKqKqKqKqKuKqKqKqKqKqKqKqKqKqKqKqKqKqKq', 'Admin', 'Sistema', 'ADMIN', true, NOW()),
                                                                                          ('user1@test.com', '$2a$10$NkM3Y8QqKqKqKqKqKqKqKuKqKqKqKqKqKqKqKqKqKqKqKqKqKq', 'Juan', 'Pérez', 'USER', true, NOW()),
                                                                                          ('user2@test.com', '$2a$10$NkM3Y8QqKqKqKqKqKqKqKuKqKqKqKqKqKqKqKqKqKqKqKqKqKq', 'María', 'García', 'USER', true, NOW()),
                                                                                          ('user3@test.com', '$2a$10$NkM3Y8QqKqKqKqKqKqKqKuKqKqKqKqKqKqKqKqKqKqKqKqKqKq', 'Carlos', 'López', 'USER', true, NOW());

-- ============================================
-- 6. VENTAS DE EJEMPLO
-- ============================================
-- Venta 1: Juan Pérez - 2 productos
INSERT INTO ventas (fecha_venta, total) VALUES
    (DATE_SUB(NOW(), INTERVAL 5 DAY), 1124.98);

INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario) VALUES
                                                                                  (1, 1, 1, 999.99),  -- Smartphone
                                                                                  (1, 8, 5, 24.99);   -- 5 Camisetas

-- Venta 2: María García - 3 productos
INSERT INTO ventas (fecha_venta, total) VALUES
    (DATE_SUB(NOW(), INTERVAL 3 DAY), 1559.96);

INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario) VALUES
                                                                                  (2, 2, 1, 1299.99), -- Laptop
                                                                                  (2, 5, 2, 349.99),  -- Auriculares
                                                                                  (2, 7, 1, 89.99);   -- Teclado

-- Venta 3: Carlos López - 1 producto
INSERT INTO ventas (fecha_venta, total) VALUES
    (DATE_SUB(NOW(), INTERVAL 1 DAY), 299.99);

INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario) VALUES
    (3, 18, 1, 299.99); -- Cocina Eléctrica

-- ============================================
-- 7. ACTUALIZAR STOCK DESPUÉS DE VENTAS
-- ============================================
UPDATE productos SET stock_actual = stock_actual - 1 WHERE id = 1;  -- Smartphone
UPDATE productos SET stock_actual = stock_actual - 5 WHERE id = 8;  -- Camisetas
UPDATE productos SET stock_actual = stock_actual - 1 WHERE id = 2;  -- Laptop
UPDATE productos SET stock_actual = stock_actual - 2 WHERE id = 5;  -- Auriculares
UPDATE productos SET stock_actual = stock_actual - 1 WHERE id = 7;  -- Teclado
UPDATE productos SET stock_actual = stock_actual - 1 WHERE id = 18; -- Cocina Eléctrica

-- ============================================
-- 8. CONSULTAS DE VERIFICACIÓN
-- ============================================
-- Verificar productos con bajo stock (menos de 30 unidades)
SELECT 'PRODUCTOS CON BAJO STOCK' as ' ';
SELECT nombre, stock_actual FROM productos WHERE stock_actual < 30 ORDER BY stock_actual ASC;

-- Verificar categorías con productos
SELECT 'CATEGORÍAS CON PRODUCTOS' as ' ';
SELECT c.nombre, COUNT(p.id) as total_productos
FROM categorias c
         LEFT JOIN productos p ON c.id = p.categoria_id
GROUP BY c.id, c.nombre;

-- Verificar ventas realizadas
SELECT 'VENTAS REALIZADAS' as ' ';
SELECT v.id, v.fecha_venta, v.total, u.nombre as vendedor
FROM ventas v
         JOIN usuarios u ON u.id = 1  -- Usuario admin por defecto
ORDER BY v.fecha_venta DESC;

-- Verificar usuarios registrados
SELECT 'USUARIOS REGISTRADOS' as ' ';
SELECT email, nombre, apellido, rol, activo FROM usuarios;

-- ============================================
-- 9. MENSAJE DE CONFIRMACIÓN
-- ============================================
SELECT 'BASE DE DATOS INICIALIZADA CORRECTAMENTE' as 'ESTADO';
SELECT CONCAT('Total Productos: ', COUNT(*)) as 'RESUMEN' FROM productos;
SELECT CONCAT('Total Categorías: ', COUNT(*)) as 'RESUMEN' FROM categorias;
SELECT CONCAT('Total Usuarios: ', COUNT(*)) as 'RESUMEN' FROM usuarios;
SELECT CONCAT('Total Ventas: ', COUNT(*)) as 'RESUMEN' FROM ventas;