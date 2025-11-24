package com.mvc.todolist.application.usecase.user;

import com.mvc.todolist.domain.model.Role;
import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.RoleRepositoryPort;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import com.mvc.todolist.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateUserRolesUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;

    public User execute(Long userId, Set<String> roleNames) {
        // Buscar el usuario
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        // Obtener los nuevos roles
        Set<Role> newRoles = new java.util.HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepositoryPort.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));
            newRoles.add(role);
        }

        // Actualizar los roles del usuario
        user.setRoles(newRoles);

        return userRepositoryPort.save(user);
    }
}

