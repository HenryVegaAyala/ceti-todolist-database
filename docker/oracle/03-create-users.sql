-- Crear tabla de usuarios
CREATE TABLE users
(
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR2(50) NOT NULL UNIQUE,
    email      VARCHAR2(100) NOT NULL UNIQUE,
    password   VARCHAR2(255) NOT NULL,
    roles      VARCHAR2(255) DEFAULT 'ROLE_USER' NOT NULL,
    enabled    NUMBER(1) DEFAULT 1 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

commit;