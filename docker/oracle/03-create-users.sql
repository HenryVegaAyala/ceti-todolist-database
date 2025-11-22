-- Crear tabla de usuarios
CREATE TABLE users
(
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR2(50) NOT NULL UNIQUE,
    email      VARCHAR2(100) NOT NULL UNIQUE,
    password   VARCHAR2(255) NOT NULL,
    enabled    NUMBER(1) DEFAULT 1 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

-- Crear tabla de roles
CREATE TABLE roles
(
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR2(50) NOT NULL UNIQUE,
    description VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índice para la tabla roles
CREATE INDEX idx_roles_name ON roles (name);

-- Crear tabla intermedia user_roles
CREATE TABLE user_roles
(
    user_id    NUMBER NOT NULL,
    role_id    NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Crear índices para la tabla intermedia
CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

-- Insertar roles por defecto
INSERT INTO roles (name, description) VALUES ('ROLE_USER', 'Usuario estándar del sistema');
INSERT INTO roles (name, description) VALUES ('ROLE_ADMIN', 'Administrador del sistema');

commit;