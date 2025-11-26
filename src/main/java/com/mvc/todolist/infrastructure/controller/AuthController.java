package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.domain.model.Role;
import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.RoleRepositoryPort;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import com.mvc.todolist.infrastructure.dto.auth.AuthResponse;
import com.mvc.todolist.infrastructure.dto.auth.LoginRequest;
import com.mvc.todolist.infrastructure.dto.auth.RegisterRequest;
import com.mvc.todolist.infrastructure.dto.auth.UserInfoResponse;
import com.mvc.todolist.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            User user = userRepositoryPort.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Set<String> roleNames = extractRoleNames(user);
            String token = generateToken(user, userDetails, roleNames);

            return ResponseEntity.ok(buildAuthResponse(user, token, roleNames));
        } catch (Exception e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas", "message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (userRepositoryPort.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(BAD_REQUEST)
                        .body(Map.of("error", "El nombre de usuario ya está en uso"));
            }

            if (userRepositoryPort.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(BAD_REQUEST)
                        .body(Map.of("error", "El email ya está en uso"));
            }

            Set<Role> userRoles = resolveRoles(request.getRoles());
            User savedUser = createAndSaveUser(request, userRoles);

            Set<String> roleNames = extractRoleNames(savedUser);
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
            String token = generateToken(savedUser, userDetails, roleNames);

            return ResponseEntity.status(CREATED).body(buildAuthResponse(savedUser, token, roleNames));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar el usuario", "message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(UNAUTHORIZED)
                        .body(Map.of("error", "No hay usuario autenticado"));
            }

            User user = userRepositoryPort.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + authentication.getName()));

            Set<String> roleNames = extractRoleNames(user);

            return ResponseEntity.ok(UserInfoResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roleNames)
                    .enabled(user.isEnabled())
                    .createdAt(user.getCreatedAt())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información del usuario", "message", e.getMessage()));
        }
    }

    private Set<String> extractRoleNames(User user) {
        return user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).collect(toSet())
                : Set.of();
    }

    private String generateToken(User user, UserDetails userDetails, Set<String> roleNames) {
        Map<String, Object> claims = Map.of("roles", roleNames, "email", user.getEmail());
        return jwtService.generateToken(claims, userDetails);
    }

    private AuthResponse buildAuthResponse(User user, String token, Set<String> roleNames) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        if (requestedRoles != null && !requestedRoles.isEmpty()) {
            return requestedRoles.stream()
                    .map(roleName -> roleRepositoryPort.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                    .collect(toSet());
        }

        Role defaultRole = roleRepositoryPort.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado en la base de datos"));
        return Set.of(defaultRole);
    }

    private User createAndSaveUser(RegisterRequest request, Set<Role> roles) {
        LocalDateTime now = LocalDateTime.now();
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return userRepositoryPort.save(newUser);
    }
}
