package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.application.usecase.user.*;
import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.infrastructure.dto.user.CreateUserRequest;
import com.mvc.todolist.infrastructure.dto.user.UpdateUserRolesRequest;
import com.mvc.todolist.infrastructure.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede acceder a todos los endpoints de este controlador
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserRolesUseCase updateUserRolesUseCase;

    /**
     * Obtener todos los usuarios
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = getAllUsersUseCase.execute()
                .stream()
                .map(UserResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /**
     * Obtener un usuario por ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return getUserByIdUseCase.execute(id)
                .map(user -> ResponseEntity.ok(UserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo usuario
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = createUserUseCase.execute(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRoles()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserResponse.fromDomain(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar los roles de un usuario
     * PUT /api/users/{id}/roles
     */
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable Long id,
            @RequestBody UpdateUserRolesRequest request) {
        try {
            User user = updateUserRolesUseCase.execute(id, request.getRoles());
            return ResponseEntity.ok(UserResponse.fromDomain(user));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un usuario
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            deleteUserUseCase.execute(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

