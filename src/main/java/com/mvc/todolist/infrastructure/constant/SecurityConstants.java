package com.mvc.todolist.infrastructure.constant;

public final class SecurityConstants {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/error",
            "/api/auth/login",
            "/actuator/**",
            "/api/health/**"
    };

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String ADMIN_ENDPOINTS = "/api/admin/**";
    public static final String TODO_ENDPOINTS = "/api/todos/**";
    public static final String USER_ENDPOINTS = "/api/users/**";

    private SecurityConstants() {
        throw new AssertionError("No se puede instanciar");
    }
}
