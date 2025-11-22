package com.mvc.todolist.infrastructure.constant;

public final class SecurityConstants {

    public static final String[] PUBLIC_ENDPOINTS = {
        "/error",
        "/api/auth/login",
        "/api/auth/register",
        "/actuator/**",
        "/api/health/**"
    };
}
