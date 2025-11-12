-- BaseDatos.sql
-- Script de creación de base de datos y esquemas

-- ========================================
-- BASE DE DATOS CLIENTE
-- ========================================

-- Crear base de datos (ejecutar como superusuario)
-- CREATE DATABASE clientedb;
-- \c clientedb;

-- Tabla Persona (herencia)
CREATE TABLE IF NOT EXISTS persona (
                                       identificacion BIGSERIAL PRIMARY KEY,
                                       nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    edad INTEGER NOT NULL,
    direccion VARCHAR(500) NOT NULL,
    telefono VARCHAR(20) NOT NULL
    );

-- Tabla Cliente (hereda de Persona)
CREATE TABLE IF NOT EXISTS cliente (
                                       identificacion BIGINT PRIMARY KEY REFERENCES persona(identificacion) ON DELETE CASCADE,
    cliente_id VARCHAR(100) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
    );

-- Índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_cliente_id ON cliente(cliente_id);
CREATE INDEX IF NOT EXISTS idx_cliente_estado ON cliente(estado);

-- ========================================
-- BASE DE DATOS CUENTA
-- ========================================

-- Crear base de datos (ejecutar como superusuario)
-- CREATE DATABASE cuentadb;
-- \c cuentadb;

-- Tabla Cuenta
CREATE TABLE IF NOT EXISTS cuenta (
                                      numero_cuenta VARCHAR(50) PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id VARCHAR(100) NOT NULL
    );

-- Tabla Movimiento
CREATE TABLE IF NOT EXISTS movimiento (
                                          id BIGSERIAL PRIMARY KEY,
                                          fecha TIMESTAMP NOT NULL,
                                          numero_cuenta VARCHAR(50) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    movimiento DECIMAL(15,2) NOT NULL,
    saldo_disponible DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (numero_cuenta) REFERENCES cuenta(numero_cuenta) ON DELETE CASCADE
    );

-- Índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_cuenta_cliente ON cuenta(cliente_id);
CREATE INDEX IF NOT EXISTS idx_cuenta_estado ON cuenta(estado);
CREATE INDEX IF NOT EXISTS idx_movimiento_cuenta ON movimiento(numero_cuenta);
CREATE INDEX IF NOT EXISTS idx_movimiento_fecha ON movimiento(fecha);
CREATE INDEX IF NOT EXISTS idx_movimiento_cuenta_fecha ON movimiento(numero_cuenta, fecha DESC);

-- ========================================
-- DATOS DE PRUEBA (Casos de Uso)
-- ========================================

-- Insertar en Cliente (conectado a clientedb)
-- Personas y Clientes según los casos de uso

INSERT INTO persona (identificacion, nombre, genero, edad, direccion, telefono) VALUES
                                                                                    (1, 'Jose Lema', 'Masculino', 30, 'Otavalo sn y principal', '098254785'),
                                                                                    (2, 'Marianela Montalvo', 'Femenino', 28, 'Amazonas y NNUU', '097548965'),
                                                                                    (3, 'Juan Osorio', 'Masculino', 35, '13 junio y Equinoccial', '098874587');

INSERT INTO cliente (identificacion, cliente_id, contrasena, estado) VALUES
                                                                         (1, 'jose123', '1234', TRUE),
                                                                         (2, 'marianela456', '5678', TRUE),
                                                                         (3, 'juan789', '1245', TRUE);

-- Insertar en Cuenta (conectado a cuentadb)
INSERT INTO cuenta (numero_cuenta, tipo, saldo_inicial, estado, cliente_id) VALUES
                                                                                ('478758', 'Ahorros', 2000.00, TRUE, 'jose123'),
                                                                                ('225487', 'Corriente', 100.00, TRUE, 'marianela456'),
                                                                                ('495878', 'Ahorros', 0.00, TRUE, 'juan789'),
                                                                                ('496825', 'Ahorros', 540.00, TRUE, 'marianela456'),
                                                                                ('585545', 'Corriente', 1000.00, TRUE, 'jose123');

-- Insertar Movimientos (según caso de uso 4)
INSERT INTO movimiento (fecha, numero_cuenta, tipo, saldo_inicial, estado, movimiento, saldo_disponible) VALUES
                                                                                                             ('2022-02-10 10:00:00', '225487', 'Corriente', 100.00, TRUE, 600.00, 700.00),
                                                                                                             ('2022-02-08 09:00:00', '496825', 'Ahorros', 540.00, TRUE, -540.00, 0.00);