-- =====================================================================
--  Sistema Web de Gestión de Activos Empresariales
--  Script de creación de base de datos - MySQL 8
--  Curso: Desarrollo Web Integrado - Avance de Proyecto Final 2
-- =====================================================================

DROP DATABASE IF EXISTS gestor_activos;
CREATE DATABASE gestor_activos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE gestor_activos;

-- ---------------------------------------------------------------------
-- 1. ROL
-- ---------------------------------------------------------------------
CREATE TABLE rol (
    id_rol   TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(30)      NOT NULL,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. EMPLEADO
-- ---------------------------------------------------------------------
CREATE TABLE empleado (
    id_empleado INT UNSIGNED NOT NULL AUTO_INCREMENT,
    dni         CHAR(8)      NOT NULL,
    nombres     VARCHAR(80)  NOT NULL,
    apellidos   VARCHAR(80)  NOT NULL,
    correo      VARCHAR(120) NOT NULL,
    area        VARCHAR(60)  NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_empleado PRIMARY KEY (id_empleado),
    CONSTRAINT uq_empleado_dni UNIQUE (dni),
    CONSTRAINT uq_empleado_correo UNIQUE (correo)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. USUARIO (acceso al sistema)
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario    INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    username      VARCHAR(40)      NOT NULL,
    password_hash VARCHAR(255)     NOT NULL,          -- PBKDF2-SHA256 con sal: pbkdf2$iter$sal$hash
    id_rol        TINYINT UNSIGNED NOT NULL,
    id_empleado   INT UNSIGNED     NULL,
    activo        BOOLEAN          NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_username UNIQUE (username),
    CONSTRAINT uq_usuario_empleado UNIQUE (id_empleado),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES rol (id_rol),
    CONSTRAINT fk_usuario_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleado (id_empleado)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. TIPO_ACTIVO
-- ---------------------------------------------------------------------
CREATE TABLE tipo_activo (
    id_tipo TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(50)      NOT NULL,
    CONSTRAINT pk_tipo_activo PRIMARY KEY (id_tipo),
    CONSTRAINT uq_tipo_activo_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. ACTIVO
-- ---------------------------------------------------------------------
CREATE TABLE activo (
    id_activo         INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(20)      NOT NULL,
    nombre            VARCHAR(100)     NOT NULL,
    descripcion       VARCHAR(255)     NULL,
    id_tipo           TINYINT UNSIGNED NOT NULL,
    estado            ENUM('DISPONIBLE','ASIGNADO','MANTENIMIENTO','BAJA')
                                       NOT NULL DEFAULT 'DISPONIBLE',
    fecha_adquisicion DATE             NOT NULL,
    valor             DECIMAL(10,2)    NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_activo PRIMARY KEY (id_activo),
    CONSTRAINT uq_activo_codigo UNIQUE (codigo),
    CONSTRAINT ck_activo_valor CHECK (valor >= 0),
    CONSTRAINT fk_activo_tipo FOREIGN KEY (id_tipo)
        REFERENCES tipo_activo (id_tipo),
    INDEX idx_activo_estado (estado)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. ASIGNACION (un activo solo puede tener UNA asignación vigente)
--    La columna generada 'vigente' vale id_activo mientras no se
--    devuelva (fecha_devolucion IS NULL) y NULL en caso contrario;
--    el índice UNIQUE sobre ella impide dos asignaciones vigentes.
-- ---------------------------------------------------------------------
CREATE TABLE asignacion (
    id_asignacion       INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_activo           INT UNSIGNED NOT NULL,
    id_empleado         INT UNSIGNED NOT NULL,
    id_usuario_registra INT UNSIGNED NOT NULL,
    fecha_asignacion    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_devolucion    DATETIME     NULL,
    observacion         VARCHAR(255) NULL,
    vigente             INT UNSIGNED GENERATED ALWAYS AS
                            (IF(fecha_devolucion IS NULL, id_activo, NULL)) STORED,
    CONSTRAINT pk_asignacion PRIMARY KEY (id_asignacion),
    CONSTRAINT uq_asignacion_vigente UNIQUE (vigente),
    CONSTRAINT fk_asig_activo FOREIGN KEY (id_activo)
        REFERENCES activo (id_activo),
    CONSTRAINT fk_asig_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleado (id_empleado),
    CONSTRAINT fk_asig_usuario FOREIGN KEY (id_usuario_registra)
        REFERENCES usuario (id_usuario),
    INDEX idx_asig_empleado (id_empleado)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. HISTORIAL_ESTADO (trazabilidad de cambios de estado)
-- ---------------------------------------------------------------------
CREATE TABLE historial_estado (
    id_historial    INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_activo       INT UNSIGNED NOT NULL,
    estado_anterior VARCHAR(15)  NULL,
    estado_nuevo    VARCHAR(15)  NOT NULL,
    fecha_cambio    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario      INT UNSIGNED NOT NULL,
    detalle         VARCHAR(255) NULL,
    CONSTRAINT pk_historial PRIMARY KEY (id_historial),
    CONSTRAINT fk_hist_activo FOREIGN KEY (id_activo)
        REFERENCES activo (id_activo),
    CONSTRAINT fk_hist_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    INDEX idx_hist_activo (id_activo, fecha_cambio)
) ENGINE=InnoDB;

-- =====================================================================
--  DATOS INICIALES DE PRUEBA
--  Credenciales: admin / admin123    |    jperez / empleado123
-- =====================================================================
INSERT INTO rol (nombre) VALUES ('ADMINISTRADOR'), ('EMPLEADO');

INSERT INTO empleado (dni, nombres, apellidos, correo, area) VALUES
 ('70000001', 'Carlos',  'Mendoza Ríos',   'cmendoza@empresa.pe', 'Sistemas'),
 ('70000002', 'Juan',    'Pérez Lara',     'jperez@empresa.pe',   'Contabilidad'),
 ('70000003', 'Lucía',   'Torres Vega',    'ltorres@empresa.pe',  'Recursos Humanos'),
 ('70000004', 'Marco',   'Quispe Ramos',   'mquispe@empresa.pe',  'Logística');

INSERT INTO usuario (username, password_hash, id_rol, id_empleado) VALUES
 ('admin',  'pbkdf2$120000$ee6a6760a800dc0bdf7d67dc07f1c216$eb920da113e0c923ab4960a6bf7a09e8600b7ff4a7379531c27736dc02b4949e',    1, 1),
 ('jperez', 'pbkdf2$120000$6933ccc92cb270b805e177c1f4aee2c6$f7a0b0fc31a3ef8fab2ad729234c6a440d040c6dc099edcaf4575dcd5a1405ef', 2, 2),
 ('ltorres','pbkdf2$120000$6933ccc92cb270b805e177c1f4aee2c6$f7a0b0fc31a3ef8fab2ad729234c6a440d040c6dc099edcaf4575dcd5a1405ef', 2, 3);

INSERT INTO tipo_activo (nombre) VALUES
 ('Equipo de cómputo'), ('Mobiliario'), ('Herramienta'), ('Vehículo'), ('Periférico');

INSERT INTO activo (codigo, nombre, descripcion, id_tipo, estado, fecha_adquisicion, valor) VALUES
 ('AC-0001', 'Laptop Dell Latitude 5420', 'Core i5, 16 GB RAM, 512 GB SSD', 1, 'ASIGNADO',      '2025-03-10', 3200.00),
 ('AC-0002', 'Laptop Lenovo ThinkPad E14', 'Ryzen 5, 8 GB RAM, 256 GB SSD', 1, 'DISPONIBLE',    '2025-06-02', 2800.00),
 ('AC-0003', 'Monitor LG 24"',             'Full HD, HDMI',                 5, 'ASIGNADO',      '2024-11-20',  450.00),
 ('AC-0004', 'Escritorio ejecutivo',       'Melamina 1.40 m',               2, 'DISPONIBLE',    '2023-08-15',  600.00),
 ('AC-0005', 'Taladro Bosch GSB 13',       'Percutor 650 W',                3, 'MANTENIMIENTO', '2022-05-30',  280.00),
 ('AC-0006', 'Impresora HP LaserJet',      'Monocromática, red',            5, 'BAJA',          '2019-01-12',  900.00);

INSERT INTO asignacion (id_activo, id_empleado, id_usuario_registra, fecha_asignacion, observacion) VALUES
 (1, 2, 1, '2025-03-12 09:00:00', 'Entrega con cargador y mochila'),
 (3, 2, 1, '2024-11-22 10:30:00', 'Monitor adicional para estación de trabajo');

INSERT INTO historial_estado (id_activo, estado_anterior, estado_nuevo, fecha_cambio, id_usuario, detalle) VALUES
 (1, NULL,         'DISPONIBLE',    '2025-03-10 08:00:00', 1, 'Registro inicial'),
 (1, 'DISPONIBLE', 'ASIGNADO',      '2025-03-12 09:00:00', 1, 'Asignado a Juan Pérez Lara'),
 (3, NULL,         'DISPONIBLE',    '2024-11-20 08:00:00', 1, 'Registro inicial'),
 (3, 'DISPONIBLE', 'ASIGNADO',      '2024-11-22 10:30:00', 1, 'Asignado a Juan Pérez Lara'),
 (5, 'DISPONIBLE', 'MANTENIMIENTO', '2026-09-01 11:00:00', 1, 'Falla en mandril'),
 (6, 'DISPONIBLE', 'BAJA',          '2026-08-15 16:00:00', 1, 'Equipo obsoleto');
