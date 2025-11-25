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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserRolesUseCase updateUserRolesUseCase;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                getAllUsersUseCase.execute()
                        .stream()
                        .map(UserResponse::fromDomain)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return getUserByIdUseCase.execute(id)
                .map(user -> ResponseEntity.ok(UserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        User user = createUserUseCase.execute(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRoles()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(@PathVariable Long id, @RequestBody UpdateUserRolesRequest request) {
        User user = updateUserRolesUseCase.execute(id, request.getRoles());
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

