package com.mvc.todolist.application.usecase.user;

import com.mvc.todolist.domain.model.Role;
import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.RoleRepositoryPort;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public User execute(String username, String email, String password, Set<String> roleNames) {
        // Validar que el username no exista
        if (userRepositoryPort.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // Validar que el email no exista
        if (userRepositoryPort.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya existe");
        }

        // Obtener los roles
        Set<Role> roles = new java.util.HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepositoryPort.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));
            roles.add(role);
        }

        // Crear el usuario
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .enabled(true)
                .build();

        return userRepositoryPort.save(user);
    }
}
