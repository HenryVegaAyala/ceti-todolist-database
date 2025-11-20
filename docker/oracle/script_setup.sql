
-- ============================================================================
-- CONFIGURACIÓN DE USUARIO DEVELOPER - TodoList Project
-- ============================================================================
-- Base de datos: Oracle Database Free (23ai)
-- Usuario: developer / developer123 (solo para desarrollo)
-- Ejecutado por: 01-setup.sh durante la inicialización del contenedor
-- ============================================================================

-- Cambiar contexto al PDB (FREEPDB1)
-- Los usuarios en Oracle multitenant deben crearse en el PDB, no en el CDB root
ALTER SESSION SET CONTAINER = FREEPDB1;

-- Eliminar usuario si existe (script idempotente)
begin
    execute immediate 'drop user developer cascade';
exception
    when others then
        if sqlcode != -1918 then  -- -1918: usuario no existe
            raise;
        end if;
end;
/

-- Crear usuario developer con permisos de desarrollo
-- ADVERTENCIA: Contraseña solo válida para desarrollo local
create user developer identified by developer123
    default tablespace users
    quota unlimited on users;

-- Otorgar roles estándar: CONNECT (conexión) y RESOURCE (crear objetos)
grant connect, resource to developer;

-- Otorgar espacio ilimitado en todos los tablespaces (solo desarrollo)
-- grant unlimited tablespace to developer;

-- Cambiar al esquema del usuario developer
ALTER SESSION SET CURRENT_SCHEMA = TODO_LIST;

commit;

