DROP DATABASE IF EXISTS acciones_elbosque;

CREATE DATABASE acciones_elbosque;
USE acciones_elbosque;

-- Tabla: mercado_interes
CREATE TABLE market_interest (
    id_mercado BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

-- Tabla: usuario
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'COMISIONISTA', 'INVERSIONISTA') NOT NULL,
    subrol ENUM('AREA_LEGAL', 'JUNTA_DIRECTIVA', 'PLATAFORMA'),
    bloqueado BOOLEAN DEFAULT FALSE,
    intentos_fallidos INT DEFAULT 0,
    reset_token VARCHAR(255) UNIQUE,
    id_mercado BIGINT,
    FOREIGN KEY (id_mercado) REFERENCES market_interest(id_mercado)
);

-- Tabla: alpaca_account
CREATE TABLE alpaca_account (
    id_alpaca_account BIGINT AUTO_INCREMENT PRIMARY KEY,
    alpaca_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255),
    currency VARCHAR(255),
    buying_power DECIMAL(18, 2),
    id_usuario BIGINT NOT NULL UNIQUE,
    created_at DATETIME,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Tabla: watchlist_item (definición completa desde el inicio)
CREATE TABLE watchlist_item (
    id_watchlist BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(255) NOT NULL,
    company_name VARCHAR(255),
    alert_min_price DOUBLE,
    alert_max_price DOUBLE,
    created_at DATETIME,
    id_usuario BIGINT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT uq_usuario_symbol UNIQUE (id_usuario, symbol)
);

-- Tabla: user_subscription 
CREATE TABLE user_subscription (
    id_user_subscription BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL UNIQUE,
    stripe_customer_id VARCHAR(255),
    stripe_subscription_id VARCHAR(255),
    activo BOOLEAN DEFAULT FALSE,
    fecha_inicio DATE,
    fecha_cancelacion DATE,
    tipo_plan VARCHAR(255), 
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Tabla: account_ach_relationship
CREATE TABLE account_ach_relationship (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME,
    ach_id VARCHAR(255),
    account_owner_name VARCHAR(255),
    bank_account_type VARCHAR(50),
    bank_account_number VARCHAR(255),
    nickname VARCHAR(255),
    alpaca_account_id BIGINT,
    FOREIGN KEY (alpaca_account_id) REFERENCES alpaca_account(id_alpaca_account)
);

-- Tabla: transfer
CREATE TABLE transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alpaca_transfer_id VARCHAR(255),
    created_at DATETIME,
    type VARCHAR(50),
    amount DECIMAL(15,2),
    status VARCHAR(30),
    currency VARCHAR(10),
    alpaca_account_id BIGINT,
    relationship_id BIGINT,
    FOREIGN KEY (alpaca_account_id) REFERENCES alpaca_account(id_alpaca_account),
    FOREIGN KEY (relationship_id) REFERENCES account_ach_relationship(id)
);

-- Tabla: orders
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alpaca_order_id VARCHAR(100),
    created_at DATETIME,
    symbol VARCHAR(20),
    asset_class VARCHAR(50),
    qty DECIMAL(15,2),
    order_type VARCHAR(20),
    side VARCHAR(10),
    time_in_force VARCHAR(20),
    limit_price DECIMAL(15,2),
    stop_price DECIMAL(15,2),
    status VARCHAR(30),
    transaction_amount DECIMAL(15,2),
    commission_amount DECIMAL(15,2),
    net_amount DECIMAL(15,2),
    alpaca_account_id BIGINT,
    FOREIGN KEY (alpaca_account_id) REFERENCES alpaca_account(id_alpaca_account)
);


-- Insertar mercados de interés
INSERT INTO market_interest (nombre) VALUES
('Tecnología'),
('Salud'),
('Finanzas'),
('Consumo Discrecional'),
('Consumo Básico'),
('Energía'),
('Industriales'),
('Materiales'),
('Servicios Públicos'),
('Bienes Raíces'),
('Telecomunicaciones');

-- Consultas útiles
USE acciones_elbosque;
SELECT * FROM usuario;
SELECT * FROM user_subscription;
SELECT * FROM alpaca_account;
SELECT * FROM market_interest;
SELECT * FROM watchlist_item;
SELECT* FROM account_ach_relationship;
SELECT * FROM transfer;
SELECT* FROM orders;

