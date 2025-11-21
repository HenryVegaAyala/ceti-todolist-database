# 📝 TodoList API - Spring Boot + Oracle

API REST para gestión de tareas (TodoList) construida con Spring Boot 3.5.7, Oracle Database y Spring Security con JWT.

## 🚀 Características

- ✅ **CRUD completo** de tareas (Todos)
- 🔐 **Autenticación JWT** con Spring Security
- 👤 **Registro y login** de usuarios
- 🏗️ **Arquitectura Hexagonal** (Puertos y Adaptadores)
- 🐳 **Oracle Database** en Docker (Oracle Free 23ai)
- 📊 **Spring Data JPA** con Hibernate
- 🛡️ **Manejo global de excepciones**
- 📈 **Spring Boot Actuator** para monitoreo
- 🔒 **Encriptación de contraseñas** con BCrypt
- 📝 **DTOs** para separación de capas
- 🚦 **Validaciones** de entrada
- 🔄 **Mappers** para conversión entre entidades y DTOs

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.5.7**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Boot Actuator
  - Spring Boot DevTools
- **Oracle Database Free 23ai** (Docker)
- **JWT (JSON Web Tokens)** - jjwt 0.12.3
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## 📋 Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6+
- Docker y Docker Compose
- Git

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
- Usuario: `developer`
- Password: `developer123`
- PDB: `FREEPDB1`
- Puerto: `1530`

### 4. Verificar que la base de datos esté lista

```bash
docker logs oracle-database
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

La aplicación estará disponible en: `http://localhost:8080`

## 📁 Estructura del Proyecto

```
todolist-oracle/
├── src/
│   ├── main/
│   │   ├── java/com/mvc/todolist/
│   │   │   ├── TodolistApplication.java
│   │   │   ├── application/
│   │   │   │   └── usecase/
│   │   │   │       └── todo/             # Casos de uso de Todos
│   │   │   │           ├── CreateTodoUseCase.java
│   │   │   │           ├── UpdateTodoUseCase.java
│   │   │   │           ├── DeleteTodoUseCase.java
│   │   │   │           ├── GetTodoByIdUseCase.java
│   │   │   │           └── GetAllTodosUseCase.java
│   │   │   ├── domain/
│   │   │   │   ├── model/                # Modelos de dominio
│   │   │   │   │   ├── Todo.java
│   │   │   │   │   └── User.java
│   │   │   │   └── port/                 # Interfaces (puertos)
│   │   │   │       ├── TodoRepositoryPort.java
│   │   │   │       └── UserRepositoryPort.java
│   │   │   └── infrastructure/
│   │   │       ├── adapter/              # Adaptadores (implementaciones)
│   │   │       │   ├── todo/
│   │   │       │   │   ├── TodoEntity.java
│   │   │       │   │   ├── TodoJpaRepository.java
│   │   │       │   │   ├── TodoMapper.java
│   │   │       │   │   └── TodoRepositoryAdapter.java
│   │   │       │   └── user/
│   │   │       │       ├── UserEntity.java
│   │   │       │       ├── UserJpaRepository.java
│   │   │       │       ├── UserMapper.java
│   │   │       │       └── UserRepositoryAdapter.java
│   │   │       ├── config/               # Configuraciones
│   │   │       │   └── JwtProperties.java
│   │   │       ├── constant/             # Constantes
│   │   │       │   └── SecurityConstants.java
│   │   │       ├── controller/           # Controladores REST
│   │   │       │   ├── TodoController.java
│   │   │       │   └── AuthController.java
│   │   │       ├── dto/                  # DTOs (Request/Response)
│   │   │       │   ├── auth/
│   │   │       │   │   ├── LoginRequest.java
│   │   │       │   │   ├── RegisterRequest.java
│   │   │       │   │   └── AuthResponse.java
│   │   │       │   └── todo/
│   │   │       │       ├── CreateTodoRequest.java
│   │   │       │       ├── UpdateTodoRequest.java
│   │   │       │       └── TodoResponse.java
│   │   │       ├── exception/            # Manejo de excepciones
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   └── ResourceNotFoundException.java
│   │   │       └── security/             # Seguridad JWT
│   │   │           ├── JwtService.java
│   │   │           ├── CustomUserDetailsService.java
│   │   │           ├── JwtAuthenticationFilter.java
│   │   │           ├── JwtAuthenticationEntryPoint.java
│   │   │           ├── JwtAuthenticationHandler.java
│   │   │           ├── JwtAccessDeniedHandler.java
│   │   │           └── SecurityConfig.java
│   │   └── resources/
│   │       └── application.properties    # Configuración de la aplicación
│   └── test/
│       └── java/com/mvc/todolist/
│           └── TodolistApplicationTests.java
├── docker/
│   └── oracle/
│       ├── 01-setup.sh                   # Script de inicialización
│       ├── 02-create-todos.sql           # Creación tabla TODOS
│       ├── 03-create-users.sql           # Creación tabla USERS
│       └── script_setup.sql              # Creación de usuario developer
├── docker-compose.yml                    # Docker Compose para Oracle
├── pom.xml                               # Dependencias Maven
├── mvnw                                  # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                              # Maven Wrapper (Windows)
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

#### Iniciar sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "juan",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "juan",
  "email": "juan@example.com",
  "roles": ["USER"]
}
```

### 🔒 Tareas (Requieren autenticación)

**Nota:** Incluir el token JWT en el header: `Authorization: Bearer <token>`

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
    "createdAt": "2025-01-21T10:00:00",
    "updatedAt": "2025-01-21T10:00:00"
  },
  {
    "id": 2,
    "title": "Implementar API REST",
    "description": "Crear endpoints con arquitectura hexagonal",
    "completed": true,
    "createdAt": "2025-01-21T11:00:00",
    "updatedAt": "2025-01-21T12:00:00"
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
  "createdAt": "2025-01-21T10:00:00",
  "updatedAt": "2025-01-21T10:00:00"
}
```

**Error - Tarea no encontrada (404 Not Found):**
```json
{
  "timestamp": "2025-01-21T10:30:00",
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
  "createdAt": "2025-01-21T14:30:00",
  "updatedAt": "2025-01-21T14:30:00"
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
  "createdAt": "2025-01-21T10:00:00",
  "updatedAt": "2025-01-21T15:00:00"
}
```

#### Eliminar una tarea
```http
DELETE /api/todos/{id}
Authorization: Bearer <token>
```

**Respuesta exitosa (204 No Content):**
```
Sin contenido
```

### 📊 Actuator (Monitoreo)

```http
GET /actuator/health
GET /actuator/info
```

## 🔐 Seguridad

### JWT Configuration

El token JWT se configura en `application.properties`:

```properties
# JWT - Válido por 24 horas (86400000 ms)
application.security.jwt.expiration=86400000
# Refresh Token - Válido por 7 días (604800000 ms)
application.security.jwt.refresh-expiration=604800000
```

### Roles de Usuario

- **USER**: Rol por defecto para usuarios registrados
- Futuros roles: ADMIN, MODERATOR (según necesidades)

## 🐳 Gestión de Docker

### Comandos útiles

```bash
# Iniciar contenedor
docker-compose up -d

# Ver logs
docker logs oracle-database

# Detener contenedor
docker-compose down

# Eliminar contenedor y volumen
docker-compose down -v

# Reiniciar contenedor
docker-compose restart
```

### Conectarse a Oracle Database

```bash
# Entrar al contenedor
docker exec -it oracle-database bash

# Conectarse con SQLPlus
sqlplus developer/developer123@FREEPDB1
```

## ⚙️ Configuración

### application.properties

```properties
# Puerto de la aplicación
server.port=8080

# Oracle Database
spring.datasource.url=jdbc:oracle:thin:@//localhost:1530/FREEPDB1
spring.datasource.username=developer
spring.datasource.password=developer123
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jpa.properties.hibernate.default_schema=DEVELOPER

# JWT
application.security.jwt.secret=<tu-secret-key>
application.security.jwt.expiration=86400000

# Logging
logging.level.com.todolist.mvc=DEBUG

# Actuator
management.endpoints.web.exposure.include=health,info
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
.\mvnw.cmd test

# Ejecutar con cobertura
.\mvnw.cmd test jacoco:report
```

## 📝 Manejo de Errores

La API implementa un manejo global de excepciones con respuestas estandarizadas:

### Formato de Error
```json
{
  "timestamp": "2025-11-21T10:30:00",
  "status": 404,
  "error": "Recurso no encontrado",
  "message": "No se encontró la tarea con ID: 123",
  "path": "/api/todos/123"
}
```

### Códigos de Estado HTTP

- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado exitosamente
- `204 No Content` - Operación exitosa sin contenido
- `400 Bad Request` - Datos inválidos o error de validación
- `401 Unauthorized` - No autenticado o token inválido
- `403 Forbidden` - No autorizado para acceder al recurso
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error interno del servidor

## 🏗️ Arquitectura Hexagonal

Este proyecto implementa **Arquitectura Hexagonal** (Puertos y Adaptadores):

- **Domain**: Lógica de negocio pura (independiente de frameworks)
  - `model/`: Entidades del dominio
  - `port/`: Interfaces (puertos)

- **Application**: Casos de uso (orquestación de la lógica)
  - `usecase/`: Casos de uso específicos

- **Infrastructure**: Detalles técnicos (dependiente de frameworks)
  - `adapter/`: Implementaciones de los puertos
  - `controller/`: API REST
  - `dto/`: Objetos de transferencia
  - `security/`: Implementación de seguridad
  - `exception/`: Manejo de errores

**Ventajas:**
- ✅ Testeable
- ✅ Mantenible
- ✅ Independiente de frameworks
- ✅ Facilita cambios tecnológicos

