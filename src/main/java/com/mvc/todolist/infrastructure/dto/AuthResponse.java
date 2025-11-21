package com.mvc.todolist.infrastructure.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class AuthResponse {

    private String token;
    private String type;
    private String username;
    private String email;
    private Set<String> roles;

    public AuthResponse() {
        this.type = "Bearer";
    }

    public AuthResponse(String token, String type, String username, String email, Set<String> roles) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}

