# 📚 Documentación de la Carpeta Security

## 📖 Índice
1. [Introducción](#introducción)
2. [Arquitectura de Seguridad](#arquitectura-de-seguridad)
3. [Componentes Principales](#componentes-principales)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Guía de Uso](#guía-de-uso)

---

## 🎯 Introducción

La carpeta `security` contiene todos los componentes necesarios para implementar un sistema de **autenticación y autorización basado en JWT (JSON Web Tokens)** en la aplicación TodoList.

### ¿Qué hace esta carpeta?
- 🔐 **Autentica usuarios** (verifica que eres quien dices ser)
- 🛡️ **Autoriza accesos** (decide qué puedes hacer)
- 🔑 **Gestiona tokens JWT** (genera y valida tokens de sesión)
- ⚠️ **Maneja errores de seguridad** (respuestas personalizadas)

---

## 🏗️ Arquitectura de Seguridad

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE (Frontend)                        │
│              Envía: Authorization: Bearer <JWT>              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              JwtAuthenticationFilter                         │
│  ✓ Extrae el token JWT del header                           │
│  ✓ Valida el token                                           │
│  ✓ Carga el usuario autenticado                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  SecurityConfig                              │
│  ✓ Define qué endpoints son públicos                         │
│  ✓ Define qué roles pueden acceder a cada endpoint           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                 CONTROLADOR                                  │
│           Procesa la petición del usuario                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 Componentes Principales

### 1️⃣ SecurityConfig.java
**🎯 Propósito:** Configuración central de toda la seguridad de la aplicación.

**¿Qué hace?**
- Define qué rutas son públicas (no requieren autenticación)
- Define qué rutas requieren roles específicos (ADMIN, USER)
- Configura el uso de JWT (sin sesiones tradicionales)
- Integra todos los componentes de seguridad

**Ejemplo de configuración:**
```java
// Endpoints públicos (sin autenticación)
/api/auth/login     ✅ Todos pueden acceder
/api/auth/register  ✅ Todos pueden acceder
/api/health/**      ✅ Todos pueden acceder

// Endpoints protegidos
/api/todos/**       🔐 Requiere rol USER o ADMIN
/api/users/**       🔐 Requiere rol ADMIN
```

**Métodos importantes:**
- `securityFilterChain()`: Configura toda la cadena de seguridad
- `authenticationProvider()`: Define cómo se autentica a los usuarios
- `passwordEncoder()`: Define cómo se encriptan las contraseñas (BCrypt con fuerza 12)

---

### 2️⃣ JwtService.java
**🎯 Propósito:** Servicio para crear y validar tokens JWT.

**¿Qué hace?**
- 🎫 **Genera tokens JWT** cuando un usuario inicia sesión
- ✅ **Valida tokens** en cada petición
- 📝 **Extrae información** del token (username, roles, fecha de expiración)

**Métodos principales:**

| Método | Descripción | Ejemplo de uso |
|--------|-------------|----------------|
| `generateToken(UserDetails)` | Crea un token JWT para un usuario | Al hacer login exitoso |
| `extractUsername(token)` | Obtiene el username del token | En cada petición protegida |
| `isTokenValid(token, user)` | Verifica si el token es válido | En el filtro de autenticación |
| `isTokenExpired(token)` | Verifica si el token expiró | Al validar un token |

**Flujo de un Token JWT:**
```
1. Usuario hace login con username/password
2. JwtService genera un token firmado
3. Token se envía al cliente
4. Cliente incluye el token en cada petición
5. JwtService valida el token en cada petición
```

---

### 3️⃣ JwtAuthenticationFilter.java
**🎯 Propósito:** Filtro que intercepta cada petición HTTP para validar el token JWT.

**¿Qué hace?**
- 🕵️ **Intercepta** todas las peticiones HTTP
- 🔍 **Busca** el token en el header `Authorization: Bearer <token>`
- ✅ **Valida** el token usando JwtService
- 👤 **Autentica** al usuario en el contexto de seguridad de Spring

**Flujo de ejecución:**
```
1. Petición HTTP llega
2. ¿Es una ruta pública? → SÍ: Deja pasar sin validar
                         → NO: Continúa al paso 3
3. ¿Tiene header Authorization? → NO: Deja pasar (será rechazada después)
                                 → SÍ: Continúa al paso 4
4. Extrae el token JWT
5. Valida el token
6. Carga el usuario y sus roles
7. Establece la autenticación en el contexto
8. Continúa con la petición
```

**Método clave:**
- `doFilterInternal()`: Lógica principal del filtro
- `shouldNotFilter()`: Define qué rutas no necesitan el filtro JWT

---

### 4️⃣ CustomUserDetailsService.java
**🎯 Propósito:** Carga los datos del usuario desde la base de datos.

**¿Qué hace?**
- 📚 **Busca** el usuario por username en la base de datos
- 🔄 **Convierte** el modelo de dominio a UserDetails de Spring Security
- 👮 **Carga** los roles y permisos del usuario

**Método principal:**
- `loadUserByUsername(username)`: Busca y retorna los datos del usuario

**Ejemplo de datos cargados:**
```java
UserDetails {
  username: "john.doe"
  password: "$2a$12$encrypted..."
  authorities: ["ROLE_USER"]
  enabled: true
  accountExpired: false
  accountLocked: false
  credentialsExpired: false
}
```

---

### 5️⃣ JwtAuthenticationEntryPoint.java
**🎯 Propósito:** Maneja errores cuando el usuario NO está autenticado.

**¿Qué hace?**
- 🚨 Se activa cuando alguien intenta acceder sin estar autenticado
- 📤 Retorna una respuesta JSON con código **401 (Unauthorized)**

**Ejemplo de respuesta:**
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 401,
  "error": "No autorizado",
  "message": "Token JWT inválido o expirado",
  "path": "/api/todos"
}
```

**Cuándo se activa:**
- Token JWT no está presente
- Token JWT es inválido
- Token JWT ha expirado
- Usuario no existe

---

### 6️⃣ JwtAccessDeniedHandler.java
**🎯 Propósito:** Maneja errores cuando el usuario está autenticado pero NO tiene permisos.

**¿Qué hace?**
- 🚫 Se activa cuando un usuario autenticado intenta acceder a algo prohibido
- 📤 Retorna una respuesta JSON con código **403 (Forbidden)**

**Ejemplo de respuesta:**
```json
{
  "timestamp": "2025-11-24T10:30:00",
  "status": 403,
  "error": "Acceso denegado",
  "message": "No tienes permisos para acceder a este recurso",
  "path": "/api/users/all"
}
```

**Cuándo se activa:**
- Usuario con rol USER intenta acceder a endpoints de ADMIN
- Usuario intenta acceder a recursos que no le pertenecen

---

### 7️⃣ SecurityUtils.java
**🎯 Propósito:** Utilidades para obtener información del usuario autenticado.

**¿Qué hace?**
- 🆔 Obtiene el username del usuario actual
- 👤 Obtiene los detalles completos del usuario actual
- ✅ Verifica si el usuario está autenticado
- 🏷️ Verifica si el usuario tiene un rol específico

**Métodos útiles:**

| Método | Retorno | Descripción |
|--------|---------|-------------|
| `getCurrentUsername()` | `Optional<String>` | Username del usuario actual |
| `getCurrentUserDetails()` | `Optional<UserDetails>` | Detalles completos del usuario |
| `isAuthenticated()` | `boolean` | ¿Está autenticado? |
| `hasRole(String)` | `boolean` | ¿Tiene este rol? |

**Ejemplo de uso:**
```java
// En un servicio o controlador
String username = SecurityUtils.getCurrentUsername()
    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

boolean isAdmin = SecurityUtils.hasRole("ADMIN");
```

---

## 🔄 Flujo de Autenticación

### 📝 1. Registro de Usuario
```
Usuario → POST /api/auth/register
       → CustomUserDetailsService guarda el usuario
       → Password encriptada con BCrypt
       → Respuesta: Usuario creado
```

### 🔐 2. Login de Usuario
```
Usuario → POST /api/auth/login con {username, password}
       → Spring Security valida credenciales
       → CustomUserDetailsService carga el usuario
       → PasswordEncoder verifica la contraseña
       → JwtService genera un token JWT
       → Respuesta: {token: "eyJhbGc...", username: "john"}
```

### 🎫 3. Acceso a Recursos Protegidos
```
Usuario → GET /api/todos con Header: Authorization: Bearer eyJhbGc...
       → JwtAuthenticationFilter intercepta
       → JwtService valida el token
       → CustomUserDetailsService carga el usuario
       → SecurityConfig verifica permisos (USER o ADMIN)
       → ✅ Acceso permitido → Controlador procesa
       → ❌ Sin permisos → JwtAccessDeniedHandler (403)
       → ❌ Token inválido → JwtAuthenticationEntryPoint (401)
```

---

## 📘 Guía de Uso

### Para Desarrolladores Frontend

#### 1. Hacer Login
```javascript
// POST /api/auth/login
const response = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'john.doe',
    password: 'mypassword'
  })
});

const data = await response.json();
const token = data.token; // Guardar este token
```

#### 2. Usar el Token en Peticiones
```javascript
// GET /api/todos
const response = await fetch('/api/todos', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

#### 3. Manejar Errores
```javascript
if (response.status === 401) {
  // Token inválido o expirado → Redirigir a login
  window.location.href = '/login';
} else if (response.status === 403) {
  // Sin permisos → Mostrar mensaje de error
  alert('No tienes permisos para esta acción');
}
```

---

### Para Desarrolladores Backend

#### 1. Proteger un Endpoint con Roles
```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // Solo ADMIN puede acceder
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        // ...
    }
}
```

#### 2. Obtener el Usuario Actual
```java
@Service
public class TodoService {
    
    public List<Todo> getMyTodos() {
        String username = SecurityUtils.getCurrentUsername()
            .orElseThrow(() -> new UnauthorizedException("No autenticado"));
        
        return todoRepository.findByUsername(username);
    }
}
```

#### 3. Configurar Nuevos Endpoints Públicos
```java
// En SecurityConstants.java
public static final String[] PUBLIC_ENDPOINTS = {
    "/api/auth/**",
    "/api/health/**",
    "/api/public/**"  // Agregar nuevas rutas públicas aquí
};
```

---

## 🔒 Configuración de Seguridad

### Niveles de Acceso

| Endpoint | Acceso |
|----------|--------|
| `/api/auth/**` | 🌍 Público (todos) |
| `/api/health/**` | 🌍 Público (todos) |
| `/api/todos/**` | 🔐 USER o ADMIN |
| `/api/users/**` | 🔐 Solo ADMIN |

### Roles Disponibles
- **USER**: Usuario estándar (puede gestionar sus propios todos)
- **ADMIN**: Administrador (acceso completo)

---

## 🛠️ Configuración Técnica

### Propiedades JWT (application.properties)
```properties
# Clave secreta para firmar tokens (debe ser segura)
jwt.secret=your-secret-key-base64-encoded

# Tiempo de expiración del token (en milisegundos)
jwt.expiration=86400000  # 24 horas
```

### Algoritmos y Seguridad
- **Algoritmo JWT**: HS256 (HMAC with SHA-256)
- **Encriptación de contraseñas**: BCrypt con fuerza 12
- **Gestión de sesiones**: Stateless (sin sesiones en servidor)
- **CORS**: Configurado para permitir peticiones desde el frontend

---

## ❓ Preguntas Frecuentes

### ¿Qué es JWT?
JWT (JSON Web Token) es un estándar para transmitir información de forma segura entre partes. Es un token que contiene información del usuario codificada y firmada.

### ¿Por qué usar JWT en lugar de sesiones?
- ✅ **Stateless**: El servidor no necesita guardar sesiones
- ✅ **Escalable**: Funciona bien en arquitecturas distribuidas
- ✅ **Móvil-friendly**: Ideal para aplicaciones móviles
- ✅ **Cross-domain**: Funciona entre diferentes dominios

### ¿Cuánto dura un token JWT?
Por defecto, 24 horas. Después el usuario debe hacer login nuevamente.

### ¿Qué pasa si alguien roba mi token?
El token tiene fecha de expiración. Además, si sospechas que fue robado, puedes:
1. Cambiar la clave secreta (invalidará todos los tokens)
2. Implementar una lista negra de tokens
3. Reducir el tiempo de expiración

### ¿Dónde guardar el token en el frontend?
- 🔐 **Recomendado**: localStorage o sessionStorage
- ⚠️ **No recomendado**: Cookies (vulnerable a CSRF)

---

## 📊 Diagrama de Clases

```
┌─────────────────────────────┐
│     SecurityConfig          │
│  - configura toda           │
│    la seguridad             │
└──────────┬──────────────────┘
           │ usa
           ▼
┌─────────────────────────────┐
│ JwtAuthenticationFilter     │
│  - intercepta peticiones    │
└──────────┬──────────────────┘
           │ usa
           ▼
┌─────────────────────────────┐      ┌──────────────────────────┐
│      JwtService             │      │ CustomUserDetailsService │
│  - genera/valida tokens     │      │  - carga usuarios BD     │
└─────────────────────────────┘      └──────────────────────────┘
           │                                    │
           └────────────┬───────────────────────┘
                        │ usan
                        ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Security Context                     │
│         (Usuario autenticado + Roles)                    │
└─────────────────────────────────────────────────────────┘
```

---

## 🎓 Conclusión

La carpeta `security` implementa un sistema completo de autenticación y autorización usando las mejores prácticas:

✅ **JWT** para autenticación stateless  
✅ **BCrypt** para encriptar contraseñas  
✅ **Roles** para controlar accesos  
✅ **Filtros** para validar cada petición  
✅ **Manejo de errores** personalizado  

Con estos componentes, la aplicación está protegida contra accesos no autorizados y garantiza que cada usuario solo pueda acceder a lo que le corresponde según su rol.

---

📅 **Última actualización**: Noviembre 2025  
👨‍💻 **Autor**: Sistema TodoList  
📧 **Contacto**: Para dudas, revisar el código o contactar al equipo de desarrollo

