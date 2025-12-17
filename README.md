# 📝 TodoList API - Spring Boot + Oracle

API REST para gestión de tareas (TodoList) construida con **Spring Boot 3.5.7**, **Oracle Database 23ai** y **Spring Security con JWT**. Este proyecto implementa una arquitectura hexagonal limpia con casos de uso separados para gestión de Todos y Usuarios.

## 🚀 Características

### Core Features
- ✅ **CRUD completo de tareas (Todos)** - Crear, leer, actualizar y eliminar tareas
- 👥 **Gestión de usuarios** - CRUD completo de usuarios con roles
- 🔐 **Autenticación JWT** con Spring Security
- 🔑 **Registro y login** de usuarios con tokens de acceso y refresh
- 🏗️ **Arquitectura Hexagonal** (Puertos y Adaptadores) - Separación clara de responsabilidades
- 🎯 **Casos de uso independientes** para cada operación

### Seguridad
- 🔒 **Encriptación de contraseñas** con BCrypt
- 🎫 **JWT Tokens** con expiración configurable (24h acceso, 7 días refresh)
- 🛡️ **Autorización basada en roles** (USER, ADMIN)
- 🚫 **Manejo de acceso denegado** y autenticación fallida
- 🌐 **CORS configurado** para integraciones frontend (React, Angular)

### Arquitectura y Calidad
- 📦 **DTOs** para separación de capas (Request/Response)
- 🔄 **Mappers** para conversión entre entidades del dominio y persistencia
- 🚦 **Validaciones** de entrada con Bean Validation
- 🛡️ **Manejo global de excepciones** con respuestas estandarizadas
- 📈 **Spring Boot Actuator** para monitoreo de salud
- 🐳 **Docker Compose** para Oracle Database con inicialización automática
- 📊 **Spring Data JPA** con Hibernate y Oracle dialect

## 🛠️ Stack Tecnológico

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework principal
  - Spring Web - API REST
  - Spring Data JPA - Persistencia
  - Spring Security - Autenticación y autorización
  - Spring Boot Actuator - Monitoreo
  - Spring Boot DevTools - Desarrollo
  - Spring Validation - Validación de datos
  
### Base de Datos
- **Oracle Database Free 23ai** - Base de datos (Docker)
- **Oracle JDBC Driver 8** - Conector JDBC
- **Hibernate** - ORM con dialect Oracle

### Seguridad
- **jjwt 0.12.3** - Generación y validación de JWT tokens
  - jjwt-api - API JWT
  - jjwt-impl - Implementación
  - jjwt-jackson - Serialización JSON

### Herramientas
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias y build
- **Docker & Docker Compose** - Containerización

## 📋 Requisitos Previos

- **Java JDK 17** o superior
- **Maven 3.6+**
- **Docker Desktop** y Docker Compose
- **Git**
- **Puerto 8080** disponible (aplicación)
- **Puerto 1530** disponible (Oracle Database)

## 🔧 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd todolist-oracle
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
COMPOSE_PROJECT_NAME=todolist-oracle
ORACLE_PASSWORD=oracle123
ORACLE_PORT=1530
```

### 3. Iniciar Oracle Database con Docker

```bash
docker-compose up -d
```

El contenedor creará automáticamente:
- **Usuario**: `developer`
- **Password**: `developer123`
- **PDB**: `FREEPDB1`
- **Puerto**: `1530`
- **Schema**: `DEVELOPER`

Los scripts SQL se ejecutarán automáticamente:
- `script_setup.sql` - Crea el usuario developer
- `01-setup.sh` - Script de inicialización
- `02-create-todos.sql` - Crea la tabla TODOS
- `03-create-users.sql` - Crea las tablas USERS y ROLES

### 4. Verificar que la base de datos esté lista

```bash
docker logs oracle-database -f
```

Espera a que aparezca el mensaje: `DATABASE IS READY TO USE!`

### 5. Compilar y ejecutar la aplicación

**En Windows PowerShell:**
```powershell
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

**En Linux/Mac:**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

## 📁 Estructura del Proyecto

```
todolist-oracle/
├── src/
│   ├── main/
│   │   ├── java/com/mvc/todolist/
│   │   │   ├── TodolistApplication.java          # Clase principal
│   │   │   ├── application/
│   │   │   │   └── usecase/                      # Casos de uso (Application Layer)
���   │   │   │       ├── todo/
│   │   │   │       │   ├── CreateTodoUseCase.java
│   │   │   │       │   ├── UpdateTodoUseCase.java
│   │   │   │       │   ├── DeleteTodoUseCase.java
│   │   │   │       │   ├── GetTodoByIdUseCase.java
│   │   │   │       │   └── GetAllTodosUseCase.java
│   │   │   │       └── user/
│   │   │   │           ├── CreateUserUseCase.java
│   │   │   │           ├── GetUserByIdUseCase.java
│   │   │   │           ├── GetAllUsersUseCase.java
│   │   │   │           ├── UpdateUserRolesUseCase.java
│   │   │   │           └── DeleteUserUseCase.java
│   │   │   ├── domain/                           # Dominio (Domain Layer)
│   │   │   │   ├── model/                        # Modelos de dominio
│   │   │   │   │   ├── Todo.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── Role.java
│   │   │   │   └── port/                         # Interfaces (puertos)
│   │   │   │       ├── TodoRepositoryPort.java
│   │   │   │       ├── UserRepositoryPort.java
│   │   │   │       └── RoleRepositoryPort.java
│   │   │   └── infrastructure/                   # Infraestructura (Infrastructure Layer)
│   │   │       ├── adapter/                      # Adaptadores de persistencia
│   │   │       │   ├── todo/
│   │   │       │   │   ├── TodoEntity.java       # Entidad JPA
│   │   │       │   │   ├── TodoJpaRepository.java
│   │   │       │   │   ├── TodoMapper.java       # Mapper Domain <-> Entity
│   │   │       │   │   └── TodoRepositoryAdapter.java
│   │   │       │   └── user/
│   │   │       │       ├── UserEntity.java
│   │   │       │       ├── RoleEntity.java
│   │   │       │       ├── UserJpaRepository.java
│   │   │       │       ├── RoleJpaRepository.java
│   │   │       │       ├── UserMapper.java
│   │   │       │       ├── RoleMapper.java
│   │   │       │       ├── UserRepositoryAdapter.java
│   │   │       │       └── RoleRepositoryAdapter.java
│   │   │       ├── config/                       # Configuraciones
│   │   │       │   ├── CorsConfig.java
│   │   │       │   └── JwtProperties.java
│   │   │       ├── constant/                     # Constantes
│   │   │       │   └── SecurityConstants.java
│   │   │       ├── controller/                   # Controladores REST
│   │   │       │   ├── AuthController.java       # Autenticación
│   │   │       │   ├── TodoController.java       # CRUD Todos
│   │   │       │   ├── UserController.java       # CRUD Users
│   │   │       │   └── HealthController.java     # Health check
│   │   │       ├── dto/                          # DTOs (Request/Response)
│   │   │       │   ├── auth/
│   │   │       │   │   ├── LoginRequest.java
│   │   │       │   │   ├── RegisterRequest.java
│   │   │       │   │   └── AuthResponse.java
│   │   │       │   ├── todo/
│   │   │       │   │   ├── CreateTodoRequest.java
│   │   │       │   │   ├── UpdateTodoRequest.java
│   │   │       │   │   └── TodoResponse.java
│   │   │       │   ├── user/
│   │   │       │   │   ├── CreateUserRequest.java
│   │   │       │   │   ├── UpdateUserRequest.java
│   │   │       │   │   └── UserResponse.java
│   │   │       │   └── health/
│   │   │       │       └── HealthResponse.java
│   │   │       ├── exception/                    # Manejo de excepciones
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   └── ResourceNotFoundException.java
│   │   │       └── security/                     # Seguridad JWT
│   │   │           ├── SecurityConfig.java       # Configuración Spring Security
│   │   │           ├── JwtService.java           # Generación y validación JWT
│   │   │           ├── CustomUserDetailsService.java
│   │   │           ├── JwtAuthenticationFilter.java
│   │   │           ├── JwtAuthenticationEntryPoint.java
│   │   │           ├── JwtAuthenticationHandler.java
│   │   │           └── JwtAccessDeniedHandler.java
│   │   └── resources/
│   │       └── application.properties            # Configuración de la aplicación
│   └── test/
│       └── java/com/mvc/todolist/
│           └── TodolistApplicationTests.java
├── docker/
│   └── oracle/
│       ├── 01-setup.sh                           # Script de inicialización
│       ├── 02-create-todos.sql                   # Creación tabla TODOS
│       ├── 03-create-users.sql                   # Creación tabla USERS y ROLES
│       └── script_setup.sql                      # Creación de usuario developer
├── docker-compose.yml                            # Docker Compose para Oracle
├── pom.xml                                       # Dependencias Maven
├── mvnw                                          # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                      # Maven Wrapper (Windows)
└── README.md
```

## 🌐 API Endpoints

### 🔓 Autenticación (Públicos)

#### Registrar usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "juan",
  "email": "juan@example.com",
  "password": "password123"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "juan",
  "email": "juan@example.com",
  "roles": ["USER"]
}
```

#### Iniciar sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "juan",
  "password": "password123"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "juan",
  "email": "juan@example.com",
  "roles": ["USER"]
}
```

### 🔐 Tareas (Requieren autenticación)

**Nota:** Todos los endpoints de tareas requieren el header: `Authorization: Bearer <token>`

#### Obtener todas las tareas
```http
GET /api/todos
Authorization: Bearer <token>
```

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Aprender Spring Boot",
    "description": "Completar el tutorial de Spring Boot con Oracle",
    "completed": false,
    "createdAt": "2025-11-24T10:00:00",
    "updatedAt": "2025-11-24T10:00:00"
  }
]
```

#### Obtener una tarea por ID
```http
GET /api/todos/{id}
Authorization: Bearer <token>
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "title": "Aprender Spring Boot",
  "description": "Completar el tutorial de Spring Boot con Oracle",
  "completed": false,
  "createdAt": "2025-11-24T10:00:00",
  "updatedAt": "2025-11-24T10:00:00"
}
```

**Error - Tarea no encontrada (404 Not Found):**
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 404,
  "error": "Recurso no encontrado",
  "message": "No se encontró la tarea con ID: 999",
  "path": "/api/todos/999"
}
```

#### Crear una nueva tarea
```http
POST /api/todos
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Aprender Spring Boot",
  "description": "Completar el tutorial de Spring Boot con Oracle"
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 3,
  "title": "Aprender Spring Boot",
  "description": "Completar el tutorial de Spring Boot con Oracle",
  "completed": false,
  "createdAt": "2025-11-24T14:30:00",
  "updatedAt": "2025-11-24T14:30:00"
}
```

#### Actualizar una tarea
```http
PUT /api/todos/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Aprender Spring Boot - Actualizado",
  "description": "Completar el tutorial avanzado",
  "completed": true
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "title": "Aprender Spring Boot - Actualizado",
  "description": "Completar el tutorial avanzado",
  "completed": true,
  "createdAt": "2025-11-24T10:00:00",
  "updatedAt": "2025-11-24T15:00:00"
}
```

#### Eliminar una tarea
```http
DELETE /api/todos/{id}
Authorization: Bearer <token>
```

**Respuesta exitosa (204 No Content):**
```
Sin contenido en el body
```

### 👥 Usuarios (Requieren autenticación)

#### Obtener todos los usuarios
```http
GET /api/users
Authorization: Bearer <token>
```

#### Obtener usuario por ID
```http
GET /api/users/{id}
Authorization: Bearer <token>
```

#### Crear usuario (admin)
```http
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "nuevo_usuario",
  "email": "nuevo@example.com",
  "password": "password123"
}
```

#### Actualizar roles de usuario (admin)
```http
PUT /api/users/{id}/roles
Authorization: Bearer <token>
Content-Type: application/json

{
  "roles": ["USER", "ADMIN"]
}
```

#### Eliminar usuario
```http
DELETE /api/users/{id}
Authorization: Bearer <token>
```

### 📊 Actuator (Monitoreo)

```http
GET /actuator/health
GET /actuator/info
```

**Respuesta Health:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Oracle",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### 🏥 Health Check Custom
```http
GET /api/health
```

**Respuesta:**
```json
{
  "status": "UP",
  "message": "TodoList API is running",
  "timestamp": "2025-11-24T10:00:00"
}
```

## 🔐 Seguridad

### Configuración JWT

El token JWT se configura en `application.properties`:

```properties
# JWT Secret Key (debe ser una clave segura de al menos 256 bits)
application.security.jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437

# JWT Token válido por 24 horas (86400000 ms)
application.security.jwt.expiration=86400000

# Refresh Token válido por 7 días (604800000 ms)
application.security.jwt.refresh-expiration=604800000
```

### Roles de Usuario

- **USER**: Rol por defecto para usuarios registrados
  - Puede gestionar sus propias tareas
  - Acceso de lectura a su perfil

- **ADMIN**: Rol administrativo
  - Acceso completo a gestión de usuarios
  - Puede asignar roles
  - Acceso a todas las operaciones

### Configuración CORS

CORS está habilitado para permitir integraciones con frontends:

```properties
application.security.cors.allowed-origins=http://localhost:3000,http://localhost:4200
application.security.cors.allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS
application.security.cors.allowed-headers=*
application.security.cors.exposed-headers=Authorization
application.security.cors.max-age=3600
```

## 🐳 Gestión de Docker

### Comandos Útiles

```bash
# Iniciar contenedor
docker-compose up -d

# Ver logs en tiempo real
docker logs oracle-database -f

# Detener contenedor
docker-compose down

# Eliminar contenedor y volumen (elimina datos)
docker-compose down -v

# Reiniciar contenedor
docker-compose restart

# Ver estado
docker-compose ps
```

### Conectarse a Oracle Database

```bash
# Entrar al contenedor
docker exec -it oracle-database bash

# Conectarse con SQLPlus como developer
sqlplus developer/developer123@FREEPDB1

# Ver tablas del schema DEVELOPER
SELECT table_name FROM user_tables;

# Ver estructura de tabla TODOS
DESC TODOS;

# Consultar todos los usuarios
SELECT * FROM USERS;
```

### Verificar Datos de Prueba

```sql
-- Ver todos los TODOs
SELECT * FROM DEVELOPER.TODOS;

-- Ver todos los usuarios y roles
SELECT u.username, u.email, r.name as role
FROM DEVELOPER.USERS u
JOIN DEVELOPER.USER_ROLES ur ON u.id = ur.user_id
JOIN DEVELOPER.ROLES r ON ur.role_id = r.id;
```

## ⚙️ Configuración

### application.properties completo

```properties
# Application
spring.application.name=todolist
aplication.version=1.0.0

# Server
server.port=8080
server.servlet.context-path=/

# Oracle Database
spring.datasource.url=jdbc:oracle:thin:@//localhost:1530/FREEPDB1
spring.datasource.username=developer
spring.datasource.password=developer123
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.default_schema=DEVELOPER
spring.jpa.open-in-view=false

# Logging
logging.level.root=INFO
logging.level.com.todolist.mvc=DEBUG
logging.level.com.mvc.todolist=DEBUG
logging.level.com.mvc.todolist.infrastructure.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.org.springframework.security=DEBUG

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# JWT
application.security.jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
application.security.jwt.expiration=86400000
application.security.jwt.refresh-expiration=604800000

# CORS
application.security.cors.allowed-origins=http://localhost:3000,http://localhost:4200
application.security.cors.allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS
application.security.cors.allowed-headers=*
application.security.cors.exposed-headers=Authorization
application.security.cors.max-age=3600
```

### Variables de Entorno (.env)

```env
COMPOSE_PROJECT_NAME=todolist-oracle
ORACLE_PASSWORD=oracle123
ORACLE_PORT=1530
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
.\mvnw.cmd test

# Ejecutar tests con output detallado
.\mvnw.cmd test -X

# Limpiar y ejecutar tests
.\mvnw.cmd clean test
```

## 🔍 Manejo de Errores

La API implementa un manejo global de excepciones con respuestas estandarizadas:

### Formato de Error
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 404,
  "error": "Recurso no encontrado",
  "message": "No se encontró la tarea con ID: 123",
  "path": "/api/todos/123"
}
```

### Códigos de Estado HTTP

| Código | Descripción | Uso |
|--------|-------------|-----|
| `200 OK` | Operación exitosa | GET, PUT exitosos |
| `201 Created` | Recurso creado | POST exitoso |
| `204 No Content` | Operación exitosa sin contenido | DELETE exitoso |
| `400 Bad Request` | Datos inválidos | Validación fallida |
| `401 Unauthorized` | No autenticado | Token faltante o inválido |
| `403 Forbidden` | No autorizado | Sin permisos suficientes |
| `404 Not Found` | Recurso no encontrado | ID no existe |
| `409 Conflict` | Conflicto de datos | Username/email duplicado |
| `500 Internal Server Error` | Error interno | Error del servidor |

### Ejemplos de Errores Comunes

**Validación fallida:**
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El título es obligatorio",
  "path": "/api/todos"
}
```

**Token JWT inválido:**
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado",
  "path": "/api/todos"
}
```

**Acceso denegado:**
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "No tiene permisos para acceder a este recurso",
  "path": "/api/users/1"
}
```

## 🏗️ Arquitectura Hexagonal

Este proyecto implementa **Arquitectura Hexagonal** (Puertos y Adaptadores):

### Capas

#### 1. Domain (Dominio) - Núcleo
- **Independiente de frameworks y librerías externas**
- `model/`: Entidades del dominio (Todo, User, Role)
- `port/`: Interfaces (puertos) que definen contratos

**Responsabilidad**: Lógica de negocio pura

#### 2. Application (Aplicación) - Casos de Uso
- **Orquesta la lógica del dominio**
- `usecase/`: Casos de uso específicos (CreateTodoUseCase, GetAllTodosUseCase, etc.)

**Responsabilidad**: Coordinación de operaciones

#### 3. Infrastructure (Infraestructura) - Detalles Técnicos
- **Dependiente de frameworks y tecnologías**
- `adapter/`: Implementaciones de los puertos (persistencia)
- `controller/`: API REST (entrada)
- `dto/`: Objetos de transferencia
- `security/`: Implementación de seguridad
- `exception/`: Manejo de errores
- `config/`: Configuraciones

**Responsabilidad**: Detalles de implementación

### Flujo de una Petición

```
Cliente HTTP
    ↓
Controller (Infrastructure)
    ↓
UseCase (Application)
    ↓
Domain Model + Port (Domain)
    ↓
Adapter → JPA Repository (Infrastructure)
    ↓
Oracle Database
```

### Ventajas

| Ventaja | Descripción |
|---------|-------------|
| ✅ **Testeable** | Fácil crear tests unitarios sin dependencias externas |
| ✅ **Mantenible** | Cambios aislados en una capa no afectan otras |
| ✅ **Independiente** | Cambiar BD o framework no afecta el dominio |
| ✅ **Escalable** | Fácil agregar nuevos casos de uso |
| ✅ **Clara** | Separación de responsabilidades evidente |

### Ejemplo Práctico

**Crear un Todo:**

1. **Controller** recibe request HTTP
2. **DTO** valida y mapea datos
3. **UseCase** ejecuta lógica de negocio
4. **Domain Model** representa el Todo
5. **Port** define contrato de persistencia
6. **Adapter** implementa persistencia con JPA
7. **Repository** guarda en Oracle

## 🐛 Solución de Problemas

### Error: `/bin/bash^M: bad interpreter`

Si al iniciar Docker ves este error:
```
/bin/bash^M: bad interpreter: No such file or directory
```

**Causa:** Los archivos `.sh` tienen finales de línea de Windows (CRLF) en lugar de Unix (LF).

**Solución rápida:**
```powershell
# Ejecutar el script automático
.\fix-line-endings.ps1

# O manualmente:
$file = '.\docker\oracle\01-setup.sh'
$content = Get-Content $file -Raw
$content = $content -replace "`r`n", "`n"
[System.IO.File]::WriteAllText($file, $content, [System.Text.UTF8Encoding]::new($false))

# Luego reinicia Docker
docker-compose down
docker-compose up -d
```

**Documentación completa:** Ver [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

### Error: "Port 1530 is already in use"

Verifica que no haya otra instancia de Oracle corriendo:
```powershell
docker ps -a
docker stop $(docker ps -q)
```

### Error de conexión a la base de datos

Asegúrate de que Oracle esté listo:
```powershell
docker logs oracle-database -f
# Espera: "DATABASE IS READY TO USE!"
```

---

## 📚 Recursos Adicionales

### Documentación Oficial
- [Spring Boot 3.x](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Oracle Database Free](https://www.oracle.com/database/free/)
- [JWT - JSON Web Tokens](https://jwt.io/)

### Arquitectura
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 👨‍💻 Autor

**Henry Vega**

## 📧 Contacto

Para preguntas o soporte, por favor abre un issue en el repositorio.

---

⭐ Si este proyecto te fue útil, por favor considera darle una estrella en GitHub!

