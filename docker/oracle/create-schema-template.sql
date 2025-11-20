-- ============================================================================
-- TEMPLATE PARA CREAR NUEVOS SCHEMAS
-- ============================================================================
-- Propósito: Plantilla genérica para crear cualquier schema adicional
-- Instrucciones:
--   1. Reemplazar {USUARIO} con el nombre del usuario (minúsculas)
--   2. Reemplazar {PASSWORD} con la contraseña deseada
--   3. Reemplazar {SCHEMA} con el nombre del schema (MAYÚSCULAS)
--   4. Personalizar permisos según necesidades
--   5. Agregar tablas y objetos específicos
-- ============================================================================

-- Cambiar al Pluggable Database
ALTER SESSION SET CONTAINER = FREEPDB1;

-- ============================================================================
-- PASO 1: ELIMINAR USUARIO SI EXISTE (Idempotencia)
-- ============================================================================
-- REEMPLAZAR: {USUARIO} con el nombre del usuario en minúsculas
begin
    execute immediate 'drop user {USUARIO} cascade';
exception
    when others then
        if sqlcode != -1918 then
            raise;
        end if;
end;
/

-- ============================================================================
-- PASO 2: CREAR USUARIO
-- ============================================================================
-- REEMPLAZAR:
--   {USUARIO} con el nombre del usuario en minúsculas
--   {PASSWORD} con la contraseña deseada
create user {USUARIO} identified by {PASSWORD}
    default tablespace users
    quota unlimited on users;

-- ============================================================================
-- PASO 3: OTORGAR PERMISOS BÁSICOS
-- ============================================================================
-- REEMPLAZAR: {USUARIO} con el nombre del usuario
grant connect, resource to {USUARIO};
grant unlimited tablespace to {USUARIO};

-- ============================================================================
-- PASO 4: PERMISOS ADICIONALES (OPCIONAL)
-- ============================================================================
-- Descomentar los permisos que necesites:

-- grant create view to {USUARIO};
-- grant create procedure to {USUARIO};
-- grant create trigger to {USUARIO};
-- grant create materialized view to {USUARIO};
-- grant create synonym to {USUARIO};
-- grant create type to {USUARIO};
-- grant create job to {USUARIO};

-- ============================================================================
-- PASO 5: PERMISOS PARA ACCEDER A OTROS SCHEMAS (OPCIONAL)
-- ============================================================================
-- Si este schema necesita acceder a objetos de otros schemas:

-- Lectura del schema DEVELOPER
-- GRANT SELECT ON DEVELOPER.TODOS TO {USUARIO};

-- Lectura y escritura del schema DEVELOPER
-- GRANT SELECT, INSERT, UPDATE, DELETE ON DEVELOPER.TODOS TO {USUARIO};

-- Acceso a secuencias
-- GRANT SELECT ON DEVELOPER.TODO_SEQ TO {USUARIO};

-- ============================================================================
-- PASO 6: CAMBIAR AL NUEVO SCHEMA
-- ============================================================================
-- REEMPLAZAR: {SCHEMA} con el nombre del schema en MAYÚSCULAS
ALTER SESSION SET CURRENT_SCHEMA = {SCHEMA};

-- ============================================================================
-- PASO 7: CREAR OBJETOS (TABLAS, SECUENCIAS, ETC.)
-- ============================================================================
-- Ejemplo: Crear una secuencia
-- CREATE SEQUENCE MI_SECUENCIA
-- START WITH 1
-- INCREMENT BY 1
-- NOCACHE
-- NOCYCLE;

-- Ejemplo: Crear una tabla
-- CREATE TABLE MI_TABLA (
--     ID NUMBER(19) PRIMARY KEY,
--     NOMBRE VARCHAR2(255) NOT NULL,
--     DESCRIPCION VARCHAR2(1000),
--     ACTIVO NUMBER(1) DEFAULT 1,
--     CREATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
--     UPDATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
-- );

-- Ejemplo: Crear índices
-- CREATE INDEX idx_mi_tabla_nombre ON MI_TABLA(NOMBRE);
-- CREATE INDEX idx_mi_tabla_created ON MI_TABLA(CREATED_AT);

-- ============================================================================
-- PASO 8: CREAR VISTAS (OPCIONAL)
-- ============================================================================
-- Ejemplo: Crear una vista
-- CREATE OR REPLACE VIEW VW_MI_VISTA AS
-- SELECT
--     ID,
--     NOMBRE,
--     DESCRIPCION,
--     CREATED_AT
-- FROM MI_TABLA
-- WHERE ACTIVO = 1
-- ORDER BY CREATED_AT DESC;

-- ============================================================================
-- PASO 9: CREAR PROCEDIMIENTOS (OPCIONAL)
-- ============================================================================
-- Ejemplo: Crear un procedimiento almacenado
-- CREATE OR REPLACE PROCEDURE SP_MI_PROCEDIMIENTO (
--     p_id IN NUMBER,
--     p_resultado OUT VARCHAR2
-- ) AS
-- BEGIN
--     SELECT NOMBRE INTO p_resultado
--     FROM MI_TABLA
--     WHERE ID = p_id;
-- EXCEPTION
--     WHEN NO_DATA_FOUND THEN
--         p_resultado := 'No encontrado';
-- END;
-- /

-- ============================================================================
-- PASO 10: INSERTAR DATOS INICIALES (OPCIONAL)
-- ============================================================================
-- INSERT INTO MI_TABLA (ID, NOMBRE, DESCRIPCION, CREATED_AT)
-- VALUES (1, 'Registro de ejemplo', 'Este es un dato inicial', SYSTIMESTAMP);

-- ============================================================================
-- PASO 11: CONFIRMAR CAMBIOS
-- ============================================================================
COMMIT;

-- ============================================================================
-- INFORMACIÓN DE CONFIGURACIÓN SPRING BOOT
-- ============================================================================
-- Para usar este schema en tu aplicación Spring Boot:
--
-- application-{profile}.properties:
-- spring.datasource.url=jdbc:oracle:thin:@//localhost:1530/FREEPDB1
-- spring.datasource.username={USUARIO}
-- spring.datasource.password={PASSWORD}
-- spring.jpa.properties.hibernate.default_schema={SCHEMA}
--
-- Ejecutar con: --spring.profiles.active={profile}
-- ============================================================================

-- ============================================================================
-- EJEMPLO DE USO COMPLETO
-- ============================================================================
-- Supongamos que quieres crear un schema llamado "reports" para reportes:
--
-- 1. Reemplazar:
--    {USUARIO} → reports_user
--    {PASSWORD} → reports123
--    {SCHEMA} → REPORTS_USER
--
-- 2. El script quedaría:
--    create user reports_user identified by reports123 ...
--    grant connect, resource to reports_user;
--    ALTER SESSION SET CURRENT_SCHEMA = REPORTS_USER;
--    CREATE TABLE MONTHLY_REPORTS (...);
--
-- 3. En Spring Boot:
--    spring.datasource.username=reports_user
--    spring.datasource.password=reports123
--    spring.jpa.properties.hibernate.default_schema=REPORTS_USER
-- ============================================================================

-- Mensaje de confirmación
SELECT 'Template de schema completado!' AS RESULTADO FROM dual;
SELECT 'Recuerda reemplazar: {USUARIO}, {PASSWORD}, {SCHEMA}' AS RECORDATORIO FROM dual;
SELECT 'Y personalizar los objetos según tus necesidades' AS RECORDATORIO FROM dual;

