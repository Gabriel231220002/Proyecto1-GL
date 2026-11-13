-- =============================================================================
-- DATOS SEMILLA: SISTEMA DE GESTIÓN DE LABORATORIOS
-- Compatible con nuevo esquema DDL de Laboratorios
-- =============================================================================

BEGIN;

-- 1. Roles
INSERT INTO rol (nombre) VALUES
('Decano'),
('Director'),
('Encargado'),
('Auxiliar'),
('Docente'),
('Universitario')
ON CONFLICT (nombre) DO NOTHING;

-- 2. Privilegios
INSERT INTO privilegio (codigo, nombre, descripcion) VALUES
('PRIV_ADMIN', 'Administrador Total', 'Acceso a todas las funciones del sistema'),
('PRIV_RESERVA', 'Gestión de Reservas', 'Permiso para solicitar y autorizar reservas'),
('PRIV_INVENTARIO', 'Gestión de Inventario', 'Permiso para registrar items e incidencias'),
('PRIV_ASISTENCIA', 'Supervisión de Asistencia', 'Permiso para registrar y supervisar asistencias')
ON CONFLICT (codigo) DO NOTHING;

-- 3. Asignación de Privilegios a Roles
INSERT INTO rol_privilegio (rol_id, privilegio_id)
SELECT r.id, p.id FROM rol r, privilegio p
WHERE r.nombre = 'Decano'
ON CONFLICT DO NOTHING;

-- 4. Usuarios
-- Contraseña hash BCrypt para 'Password123*'
INSERT INTO usuario (nombres, apellido_p, apellido_m, genero, foto, ci, fecha_nacimiento, telefono, email, password_hash, registro_institucional, estado) VALUES
('Carlos', 'Montaño', 'Pérez', 'M', '', 1000001, '1975-04-12', 70000001, 'decano@uagrm.edu.bo', '$2y$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2000001, TRUE),
('Patricia', 'Gutiérrez', 'Rojas', 'F', '', 1000002, '1980-08-20', 70000002, 'directora.sistemas@uagrm.edu.bo', '$2y$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2000002, TRUE),
('Roberto', 'Fernández', 'Suárez', 'M', '', 1000003, '1988-11-05', 70000003, 'encargado.lab@uagrm.edu.bo', '$2y$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2000003, TRUE),
('Mateo', 'Mendoza', 'Flores', 'M', '', 1000004, '2001-02-14', 70000004, 'auxiliar.mateo@uagrm.edu.bo', '$2y$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2000004, TRUE),
('Julio', 'Vallejos', 'Morales', 'M', '', 1000005, '1978-06-30', 70000005, 'jvallejos@uagrm.edu.bo', '$2y$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2000005, TRUE),
('Alejandra', 'Paz', 'Vaca', 'F', '', 1000006, '2003-09-18', 70000006, 'alejandra.paz@uagrm.edu.bo', '$2y$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2000006, TRUE)
ON CONFLICT (email) DO NOTHING;

-- 5. Asignación de Roles Principales
INSERT INTO usuario_rol (usuario_id, rol_id, es_principal)
SELECT u.id, r.id, TRUE
FROM usuario u, rol r
WHERE (u.email = 'decano@uagrm.edu.bo' AND r.nombre = 'Decano')
   OR (u.email = 'directora.sistemas@uagrm.edu.bo' AND r.nombre = 'Director')
   OR (u.email = 'encargado.lab@uagrm.edu.bo' AND r.nombre = 'Encargado')
   OR (u.email = 'auxiliar.mateo@uagrm.edu.bo' AND r.nombre = 'Auxiliar')
   OR (u.email = 'jvallejos@uagrm.edu.bo' AND r.nombre = 'Docente')
   OR (u.email = 'alejandra.paz@uagrm.edu.bo' AND r.nombre = 'Universitario')
ON CONFLICT (usuario_id, rol_id) DO NOTHING;

-- 6. Carreras
INSERT INTO carrera (codigo, nombre, sigla) VALUES
('101', 'Ingeniería de Sistemas', 'SIS'),
('102', 'Ingeniería Informática', 'INF'),
('103', 'Ingeniería en Redes y Telecomunicaciones', 'RED'),
('104', 'Ingeniería en Robótica', 'ROB')
ON CONFLICT (codigo) DO NOTHING;

-- 7. Periodo Académico
INSERT INTO periodo (gestion, semestre, aprobado, fecha_apertura, fecha_finalizacion, estado) VALUES
(2026, 2, FALSE, '2026-08-01', '2026-12-20', TRUE)
ON CONFLICT DO NOTHING;

-- 8. Laboratorios
INSERT INTO labarotario (nombre, hora_apertura, hora_cierre) VALUES
('Laboratorio de Software 1', '07:00:00', '21:30:00'),
('Laboratorio de Redes y Servidores', '07:00:00', '21:30:00'),
('Laboratorio de Hardware y Robótica', '07:00:00', '21:30:00')
ON CONFLICT DO NOTHING;

-- 9. Horarios
INSERT INTO horario (dia, hora_inicio, hora_fin, labarotario_id) VALUES
('LUNES', '07:00:00', '09:15:00', 1),
('LUNES', '09:15:00', '11:30:00', 1),
('LUNES', '11:30:00', '13:45:00', 1),
('MARTES', '07:00:00', '09:15:00', 2),
('MARTES', '09:15:00', '11:30:00', 2),
('MIERCOLES', '07:00:00', '09:15:00', 3),
('MIERCOLES', '09:15:00', '11:30:00', 3)
ON CONFLICT DO NOTHING;

-- 10. Items de Inventario
INSERT INTO item_inventario (labarotario_id, codigo, nombre, descripcion, estado_fisico) VALUES
(1, 'PC-LAB1-01', 'Computadora All-in-One Dell Core i7 16GB', 'Equipo principal hilera 1', 'BUENO'),
(1, 'PC-LAB1-02', 'Computadora All-in-One Dell Core i7 16GB', 'Equipo secundario hilera 1', 'BUENO'),
(1, 'PROY-LAB1', 'Proyector Epson PowerLite', 'Proyector montado en techo', 'BUENO'),
(2, 'RACK-LAB2-01', 'Router Cisco 2911', 'Router de laboratorio para prácticas de enrutamiento', 'BUENO'),
(2, 'SW-LAB2-01', 'Switch Cisco Catalyst 2960', 'Switch 24 puertos Gigabit', 'REGULAR')
ON CONFLICT (codigo) DO NOTHING;

COMMIT;
