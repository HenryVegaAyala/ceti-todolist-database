# 🚀 Referencia Rápida - Seguridad

## 📁 Archivos en la Carpeta Security

### 1. SecurityConfig.java ⚙️
**En pocas palabras:** El "cerebro" de la seguridad que configura todo.

**Hace:**
- Define rutas públicas vs protegidas
- Asigna roles a endpoints
- Configura JWT sin sesiones

**Importante saber:**
- `/api/auth/**` → Público ✅
- `/api/todos/**` → Requiere USER o ADMIN 🔐
- `/api/users/**` → Solo ADMIN 🔐

---

### 2. JwtService.java 🎫
**En pocas palabras:** Crea y valida los tokens JWT.

**Hace:**
- Genera token cuando haces login ✨
- Valida token en cada petición ✅
- Extrae información del token (username, roles) 📝

**Métodos clave:**
```java
generateToken(user)     // Crea un token nuevo
isTokenValid(token)     // ¿Es válido este token?
extractUsername(token)  // ¿Quién es este usuario?
```

---

### 3. JwtAuthenticationFilter.java 🛡️
**En pocas palabras:** El "guardia de seguridad" que revisa cada petición.

**Hace:**
- Intercepta TODAS las peticiones HTTP 🚦
- Busca el token en el header `Authorization: Bearer <token>`
- Valida el token y autentica al usuario
- Si no hay token o es inválido → rechaza ❌

**Flujo:**
```
Petición → ¿Ruta pública? → SÍ → Deja pasar ✅
                          → NO → ¿Tiene token válido? → SÍ → Autentica ✅
                                                       → NO → Rechaza ❌
```

---

### 4. CustomUserDetailsService.java 👤
**En pocas palabras:** Busca y carga los datos del usuario.

**Hace:**
- Busca usuario en la base de datos 🔍
- Carga sus roles y permisos 🏷️
- Convierte a formato que entiende Spring Security

**Cuándo se usa:**
- Al hacer login 🔐
- Al validar un token 🎫
- Al cargar permisos de usuario 👮

---

### 5. JwtAuthenticationEntryPoint.java 🚨
**En pocas palabras:** Responde cuando NO estás autenticado.

**Hace:**
- Se activa cuando intentas acceder sin token válido
- Devuelve error **401 Unauthorized**
- Mensaje: "Necesitas estar autenticado"

**Ejemplo de respuesta:**
```json
{
  "status": 401,
  "error": "No autorizado",
  "message": "Token JWT inválido o expirado"
}
```

---

### 6. JwtAccessDeniedHandler.java 🚫
**En pocas palabras:** Responde cuando NO tienes permisos.

**Hace:**
- Se activa cuando estás autenticado pero sin permisos
- Devuelve error **403 Forbidden**
- Mensaje: "No tienes permisos para esto"

**Ejemplo de respuesta:**
```json
{
  "status": 403,
  "error": "Acceso denegado",
  "message": "No tienes permisos para acceder a este recurso"
}
```

---

### 7. SecurityUtils.java 🔧
**En pocas palabras:** Utilidades para obtener info del usuario actual.

**Hace:**
- Obtiene el username del usuario actual 🆔
- Verifica si está autenticado ✅
- Verifica si tiene cierto rol 🏷️

**Métodos útiles:**
```java
SecurityUtils.getCurrentUsername()    // → "john.doe"
SecurityUtils.isAuthenticated()       // → true/false
SecurityUtils.hasRole("ADMIN")        // → true/false
```

---

## 🔄 Flujo Completo en 3 Pasos

### 1️⃣ LOGIN
```
Usuario → POST /api/auth/login con {username, password}
       → CustomUserDetailsService verifica credenciales
       → JwtService genera un token
       → Usuario recibe: {token: "eyJhbGc..."}
```

### 2️⃣ GUARDAR TOKEN
```
Frontend guarda el token en localStorage o sessionStorage
```

### 3️⃣ USAR TOKEN
```
Usuario → GET /api/todos 
         Header: Authorization: Bearer eyJhbGc...
       → JwtAuthenticationFilter intercepta
       → JwtService valida token
       → SecurityConfig verifica permisos
       → ✅ Acceso permitido → Procesa petición
       → ❌ Token inválido → 401 Unauthorized
       → ❌ Sin permisos → 403 Forbidden
```

---

## 🎯 Casos de Uso Comunes

### Usuario Normal (Rol USER)
✅ Puede hacer login  
✅ Puede ver/crear/editar/borrar SUS todos  
❌ No puede ver todos de otros usuarios  
❌ No puede acceder a /api/users/**  

### Administrador (Rol ADMIN)
✅ Puede hacer login  
✅ Puede ver/crear/editar/borrar TODOS los todos  
✅ Puede gestionar usuarios  
✅ Puede acceder a todo  

---

## 📊 Tabla de Respuestas HTTP

| Código | Nombre | Cuándo ocurre | Qué hacer |
|--------|--------|---------------|-----------|
| 200 | OK | Todo bien | Continuar |
| 401 | Unauthorized | Token inválido/expirado | Redirigir a login |
| 403 | Forbidden | Sin permisos para este recurso | Mostrar error |
| 404 | Not Found | Recurso no existe | Verificar URL |
| 500 | Server Error | Error del servidor | Reportar error |

---

## 💡 Tips para Desarrolladores

### Frontend
1. **Guardar token después del login**
   ```javascript
   localStorage.setItem('token', response.token);
   ```

2. **Incluir token en cada petición**
   ```javascript
   headers: {
     'Authorization': `Bearer ${localStorage.getItem('token')}`
   }
   ```

3. **Manejar expiración de token**
   ```javascript
   if (response.status === 401) {
     localStorage.removeItem('token');
     window.location.href = '/login';
   }
   ```

### Backend
1. **Obtener usuario actual**
   ```java
   String username = SecurityUtils.getCurrentUsername()
       .orElseThrow(() -> new UnauthorizedException());
   ```

2. **Verificar rol**
   ```java
   if (SecurityUtils.hasRole("ADMIN")) {
       // Lógica solo para admin
   }
   ```

3. **Proteger endpoint con anotación**
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   @GetMapping("/admin-only")
   public String adminEndpoint() { ... }
   ```

---

## 🔐 Seguridad en 5 Puntos

1. **Contraseñas**: Siempre encriptadas con BCrypt ✅
2. **Tokens**: Firmados y con expiración (24h) ✅
3. **HTTPS**: Usar siempre en producción 🔒
4. **Secreto JWT**: Mantenerlo privado y seguro 🤐
5. **Roles**: Verificar permisos en cada endpoint ✅

---

## ⚡ Solución de Problemas

### Error: 401 Unauthorized
**Causas:**
- Token no incluido en header
- Token expirado (más de 24h)
- Token inválido o corrupto
- Usuario no existe

**Solución:** Hacer login nuevamente

### Error: 403 Forbidden
**Causas:**
- Estás autenticado pero sin permisos
- Intentas acceder a recurso de otro usuario
- Tu rol no tiene acceso a ese endpoint

**Solución:** Verificar que tienes el rol correcto

### Token no funciona
**Verificar:**
1. ¿Incluiste `Bearer ` antes del token?
2. ¿El token no tiene espacios extras?
3. ¿El token no expiró?
4. ¿La clave secreta no cambió?

---

## 📝 Checklist de Implementación

### Para agregar un nuevo endpoint protegido:
- [ ] Crear el controlador
- [ ] Decidir qué rol puede acceder
- [ ] Agregar la ruta a SecurityConstants si es necesario
- [ ] Probar con Postman/Frontend
- [ ] Verificar que funciona con diferentes roles

### Para agregar un nuevo rol:
- [ ] Crear el rol en la base de datos
- [ ] Agregar constante en SecurityConstants
- [ ] Actualizar SecurityConfig con las rutas
- [ ] Probar accesos y restricciones

---

## 🎓 Conceptos Clave

**JWT (JSON Web Token)**: Token que contiene información del usuario, firmado digitalmente.

**Bearer Token**: Forma de enviar el token en el header: `Authorization: Bearer <token>`

**Stateless**: El servidor no guarda sesiones, toda la info está en el token.

**BCrypt**: Algoritmo para encriptar contraseñas de forma segura.

**Spring Security**: Framework de Spring para manejar autenticación y autorización.

**Roles**: Etiquetas que definen qué puede hacer un usuario (USER, ADMIN).

---

📚 **Documentación completa**: Ver `SECURITY_DOCUMENTATION.md`  
🐛 **Reportar problemas**: Contactar al equipo de desarrollo  
✨ **Última actualización**: Noviembre 2025

