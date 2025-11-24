package com.mvc.todolist.infrastructure.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles; // Nombres de roles, ej: ["ROLE_USER", "ROLE_ADMIN"]
}
