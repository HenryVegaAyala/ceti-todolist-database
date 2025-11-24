package com.mvc.todolist.infrastructure.constant;

public final class SecurityConstants {

    // Endpoints públicos (sin autenticación)
    public static final String[] PUBLIC_ENDPOINTS = {
        "/error",
        "/api/auth/login",
        "/actuator/**",
        "/api/health/**"
    };

    // Roles del sistema (sin prefijo ROLE_ para uso en hasRole())
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Endpoints por rol
    public static final String ADMIN_ENDPOINTS = "/api/admin/**";
    public static final String TODO_ENDPOINTS = "/api/todos/**";
    public static final String USER_ENDPOINTS = "/api/users/**";

    private SecurityConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no puede ser instanciada");
    }
}
