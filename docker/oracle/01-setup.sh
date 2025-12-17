#!/bin/bash
# Script de inicializacion para Oracle Database
# Se ejecuta automaticamente cuando el contenedor se construye

echo "Esperando a que Oracle Database esa disponible..."

# Esperar a que la base de datos estÃ© lista
while ! sqlplus -s sys/${ORACLE_PASSWORD}@//localhost:1521/FREE as sysdba <<< "SELECT 1 FROM dual;" > /dev/null 2>&1; do
    echo "Esperando conexion a Oracle..."
    sleep 5
done

echo "Oracle Database disponible. Ejecutando script de setup..."

# Ejecutar el script SQL
sqlplus -s sys/${ORACLE_PASSWORD}@//localhost:1521/FREE as sysdba <<EOF
@/container-entrypoint-initdb.d/setup/01-script_setup.sql
EXIT;
EOF

echo "Script de setup ejecutado exitosamente."

