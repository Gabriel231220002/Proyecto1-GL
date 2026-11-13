-- ==============================================================================
-- ESQUEMA DE BASE DE DATOS: SISTEMA DE GESTIÓN DE LABORATORIOS DE FACULTAD
-- Compatible con script_gestion_laboratorio.txt y Modelos del Sistema
-- ==============================================================================

BEGIN;

DROP TABLE IF EXISTS asistencia CASCADE;
DROP TABLE IF EXISTS usuario_horario CASCADE;
DROP TABLE IF EXISTS permuta_turno_horario CASCADE;
DROP TABLE IF EXISTS permuta_turno CASCADE;
DROP TABLE IF EXISTS reserva_horario CASCADE;
DROP TABLE IF EXISTS reserva CASCADE;
DROP TABLE IF EXISTS ticket_incidencia CASCADE;
DROP TABLE IF EXISTS objeto_perdido CASCADE;
DROP TABLE IF EXISTS item_inventario CASCADE;
DROP TABLE IF EXISTS grupo_horario CASCADE;
DROP TABLE IF EXISTS horario CASCADE;
DROP TABLE IF EXISTS labarotario CASCADE;
DROP TABLE IF EXISTS grupo CASCADE;
DROP TABLE IF EXISTS periodo_cierre_laboratorio CASCADE;
DROP TABLE IF EXISTS cierre_laboratorio CASCADE;
DROP TABLE IF EXISTS periodo CASCADE;
DROP TABLE IF EXISTS carrera_materia CASCADE;
DROP TABLE IF EXISTS materia CASCADE;
DROP TABLE IF EXISTS usuario_carrera CASCADE;
DROP TABLE IF EXISTS carrera CASCADE;
DROP TABLE IF EXISTS usuario_rol CASCADE;
DROP TABLE IF EXISTS rol_privilegio CASCADE;
DROP TABLE IF EXISTS privilegio CASCADE;
DROP TABLE IF EXISTS rol CASCADE;
DROP TABLE IF EXISTS chat CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- 1. TABLAS PRINCIPALES (SIN LLAVES FORÁNEAS)
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nombres VARCHAR(64) NOT NULL,
    apellido_p VARCHAR(30) NOT NULL,
    apellido_m VARCHAR(30) NOT NULL,
    genero CHAR(1) NOT NULL,
    foto TEXT,
    ci INTEGER UNIQUE NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono INTEGER UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    registro_institucional INTEGER UNIQUE NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE privilegio (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL
);

CREATE TABLE carrera (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL,
    sigla VARCHAR(20) NOT NULL
);

CREATE TABLE materia (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(64) NOT NULL,
    sigla VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE periodo (
    id SERIAL PRIMARY KEY,
    gestion INTEGER NOT NULL,
    semestre INTEGER NOT NULL,
    aprobado BOOLEAN DEFAULT FALSE,
    fecha_apertura DATE NOT NULL,
    fecha_finalizacion DATE NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE labarotario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL
);

CREATE TABLE cierre_laboratorio (
    id SERIAL PRIMARY KEY,
    motivo VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL
);

-- 2. TABLAS CON LLAVES FORÁNEAS DIRECTAS
CREATE TABLE horario (
    id SERIAL PRIMARY KEY,
    dia VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    labarotario_id INTEGER REFERENCES labarotario(id) ON DELETE SET NULL
);

CREATE TABLE item_inventario (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(64) NOT NULL,
    descripcion TEXT NOT NULL,
    estado_fisico VARCHAR(50) NOT NULL,
    foto TEXT,
    labarotario_id INTEGER NOT NULL REFERENCES labarotario(id) ON DELETE CASCADE
);

CREATE TABLE chat (
    id SERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_envio DATE NOT NULL DEFAULT CURRENT_DATE,
    estado_lectura BOOLEAN DEFAULT FALSE,
    remitente_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    destinatario_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE ticket_incidencia (
    id SERIAL PRIMARY KEY,
    asunto VARCHAR(64) NOT NULL,
    descripcion TEXT NOT NULL,
    solucion_aplicada TEXT,
    fecha_incidencia DATE NOT NULL,
    fecha_solucion DATE,
    usuario_reporta_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    usuario_atiende_id INTEGER REFERENCES usuario(id) ON DELETE SET NULL,
    item_inventario_id INTEGER NOT NULL REFERENCES item_inventario(id) ON DELETE CASCADE
);

CREATE TABLE objeto_perdido (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(64) NOT NULL,
    descripcion_objeto TEXT,
    estado_devolucion BOOLEAN DEFAULT FALSE,
    fecha_hallazgo DATE NOT NULL,
    fecha_entrega DATE,
    foto TEXT,
    usuario_registra_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    usuario_reclama_id INTEGER REFERENCES usuario(id) ON DELETE SET NULL,
    labarotario_id INTEGER NOT NULL REFERENCES labarotario(id) ON DELETE CASCADE
);

CREATE TABLE permuta_turno (
    id SERIAL PRIMARY KEY,
    fecha_ausencia DATE NOT NULL,
    motivo TEXT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud DATE NOT NULL,
    usuario_solicitante_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    usuario_receptor_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE reserva (
    id SERIAL PRIMARY KEY,
    fecha_solicitud DATE NOT NULL,
    usuario_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    usuario_registro_id INTEGER REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE TABLE grupo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    materia_id INTEGER NOT NULL REFERENCES materia(id) ON DELETE CASCADE,
    periodo_id INTEGER NOT NULL REFERENCES periodo(id) ON DELETE CASCADE,
    usuario_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE
);

-- 3. CLASES DE ASOCIACIÓN Y TABLAS DEPENDIENTES
CREATE TABLE periodo_cierre_laboratorio (
    periodo_id INTEGER NOT NULL REFERENCES periodo(id) ON DELETE CASCADE,
    cierre_laboratorio_id INTEGER NOT NULL REFERENCES cierre_laboratorio(id) ON DELETE CASCADE,
    PRIMARY KEY (periodo_id, cierre_laboratorio_id)
);

CREATE TABLE usuario_rol (
    usuario_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    rol_id INTEGER NOT NULL REFERENCES rol(id) ON DELETE CASCADE,
    es_principal BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (usuario_id, rol_id)
);

CREATE TABLE rol_privilegio (
    rol_id INTEGER NOT NULL REFERENCES rol(id) ON DELETE CASCADE,
    privilegio_id INTEGER NOT NULL REFERENCES privilegio(id) ON DELETE CASCADE,
    PRIMARY KEY (rol_id, privilegio_id)
);

CREATE TABLE usuario_carrera (
    usuario_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    carrera_id INTEGER NOT NULL REFERENCES carrera(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, carrera_id)
);

CREATE TABLE carrera_materia (
    carrera_id INTEGER NOT NULL REFERENCES carrera(id) ON DELETE CASCADE,
    materia_id INTEGER NOT NULL REFERENCES materia(id) ON DELETE CASCADE,
    nivel_academico INTEGER NOT NULL,
    PRIMARY KEY (carrera_id, materia_id)
);

CREATE TABLE grupo_horario (
    grupo_id INTEGER NOT NULL REFERENCES grupo(id) ON DELETE CASCADE,
    horario_id INTEGER NOT NULL REFERENCES horario(id) ON DELETE CASCADE,
    estado BOOLEAN DEFAULT TRUE,
    es_parcial BOOLEAN DEFAULT FALSE,
    fecha_parcial DATE,
    PRIMARY KEY (grupo_id, horario_id)
);

CREATE TABLE reserva_horario (
    reserva_id INTEGER NOT NULL REFERENCES reserva(id) ON DELETE CASCADE,
    horario_id INTEGER NOT NULL REFERENCES horario(id) ON DELETE CASCADE,
    fecha_uso DATE NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    motivo TEXT,
    PRIMARY KEY (reserva_id, horario_id)
);

CREATE TABLE permuta_turno_horario (
    permuta_turno_id INTEGER NOT NULL REFERENCES permuta_turno(id) ON DELETE CASCADE,
    horario_id INTEGER NOT NULL REFERENCES horario(id) ON DELETE CASCADE,
    PRIMARY KEY (permuta_turno_id, horario_id)
);

CREATE TABLE usuario_horario (
    usuario_id INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    horario_id INTEGER NOT NULL REFERENCES horario(id) ON DELETE CASCADE,
    tipo_turno VARCHAR(20) NOT NULL,
    PRIMARY KEY (usuario_id, horario_id)
);

CREATE TABLE asistencia (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    estado_asistencia VARCHAR(50) NOT NULL,
    justificacion TEXT,
    es_falta_justificada BOOLEAN DEFAULT TRUE,
    usuario_id INTEGER NOT NULL,
    horario_id INTEGER NOT NULL,
    FOREIGN KEY (usuario_id, horario_id) 
        REFERENCES usuario_horario(usuario_id, horario_id) ON DELETE CASCADE
);

COMMIT;
