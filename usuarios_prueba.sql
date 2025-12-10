-- ============================================
-- SCRIPT SQL PARA CREAR USUARIOS DE PRUEBA
-- Base de Datos: MySQL
-- Tabla: users
-- ============================================

-- Crear la tabla users (si no existe)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- INSERTAR USUARIOS DE PRUEBA
-- ============================================

-- Usuario 1: Administrador
INSERT INTO users (name, email, clave, is_admin, created_at)
VALUES ('Admin User', 'admin@example.com', 'admin123', 1, NOW());

-- Usuario 2: Usuario normal
INSERT INTO users (name, email, clave, is_admin, created_at)
VALUES ('Usuario Test', 'usuario@example.com', 'password', 0, NOW());

-- Usuario 3: Usuario de prueba
INSERT INTO users (name, email, clave, is_admin, created_at)
VALUES ('Juan Pérez', 'juan@test.com', '12345', 0, NOW());

-- Usuario 4: Usuario de prueba 2
INSERT INTO users (name, email, clave, is_admin, created_at)
VALUES ('María González', 'maria@test.com', 'maria123', 0, NOW());

-- Usuario 5: Otro administrador
INSERT INTO users (name, email, clave, is_admin, created_at)
VALUES ('Super Admin', 'superadmin@example.com', 'super123', 1, NOW());

-- ============================================
-- VERIFICAR LOS USUARIOS CREADOS
-- ============================================

SELECT * FROM users;

-- ============================================
-- CREDENCIALES PARA PROBAR EN LA APP
-- ============================================

-- 👤 ADMINISTRADOR:
--    Email: admin@example.com
--    Clave: admin123
--    isAdmin: true

-- 👤 USUARIO NORMAL:
--    Email: usuario@example.com
--    Clave: password
--    isAdmin: false

-- 👤 JUAN:
--    Email: juan@test.com
--    Clave: 12345
--    isAdmin: false

-- 👤 MARÍA:
--    Email: maria@test.com
--    Clave: maria123
--    isAdmin: false

-- 👤 SUPER ADMIN:
--    Email: superadmin@example.com
--    Clave: super123
--    isAdmin: true

-- ============================================
-- CONSULTAS ÚTILES
-- ============================================

-- Ver todos los usuarios
SELECT id, name, email, is_admin, created_at FROM users;

-- Ver solo administradores
SELECT * FROM users WHERE is_admin = 1;

-- Ver solo usuarios normales
SELECT * FROM users WHERE is_admin = 0;

-- Buscar usuario por email
SELECT * FROM users WHERE email = 'admin@example.com';

-- Contar usuarios
SELECT COUNT(*) as total_usuarios FROM users;

-- Eliminar todos los usuarios (¡CUIDADO!)
-- DELETE FROM users;

-- Eliminar un usuario específico
-- DELETE FROM users WHERE email = 'usuario@example.com';

-- ============================================
-- ACTUALIZAR CONTRASEÑA DE UN USUARIO
-- ============================================

-- Cambiar contraseña del admin
-- UPDATE users SET clave = 'nueva_clave' WHERE email = 'admin@example.com';

-- ============================================
-- HACER A UN USUARIO ADMINISTRADOR
-- ============================================

-- UPDATE users SET is_admin = 1 WHERE email = 'juan@test.com';

-- ============================================
-- NOTA IMPORTANTE: SEGURIDAD
-- ============================================

-- ⚠️ ESTE CÓDIGO ES SOLO PARA DESARROLLO
--
-- En PRODUCCIÓN deberías:
-- 1. Encriptar las contraseñas con BCrypt
-- 2. Nunca guardar contraseñas en texto plano
-- 3. Usar contraseñas fuertes
--
-- Ejemplo con BCrypt en Spring Boot:
--
-- @Autowired
-- private PasswordEncoder passwordEncoder;
--
-- String hashedPassword = passwordEncoder.encode("admin123");
-- // Resultado: $2a$10$abcdefghijklmnopqrstuvwxyz...
--
-- Para verificar:
-- boolean matches = passwordEncoder.matches("admin123", hashedPassword);

