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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            // Autenticar al usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Cargar el usuario autenticado
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            // Buscar el usuario en la base de datos
            User user = userRepositoryPort.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Log de depuración
            System.out.println("Usuario encontrado: " + user.getUsername());
            System.out.println("Roles del usuario: " + user.getRoles());
            System.out.println("Cantidad de roles: " + (user.getRoles() != null ? user.getRoles().size() : "null"));

            // Extraer nombres de roles
            Set<String> roleNames = user.getRoles() != null
                    ? user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
                    : new HashSet<>();

            System.out.println("Nombres de roles extraídos: " + roleNames);

            // Generar token JWT
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", roleNames);
            extraClaims.put("email", user.getEmail());

            String token = jwtService.generateToken(extraClaims, userDetails);

            // Crear respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roleNames)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciales inválidas");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Verificar si el usuario ya existe
            if (userRepositoryPort.existsByUsername(request.getUsername())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "El nombre de usuario ya está en uso");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            if (userRepositoryPort.existsByEmail(request.getEmail())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "El email ya está en uso");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Obtener roles desde la base de datos
            Set<Role> userRoles = new HashSet<>();
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                for (String roleName : request.getRoles()) {
                    Role role = roleRepositoryPort.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                    userRoles.add(role);
                }
            } else {
                // Asignar rol por defecto ROLE_USER
                Role defaultRole = roleRepositoryPort.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado en la base de datos"));
                userRoles.add(defaultRole);
            }

            // Crear el nuevo usuario
            User newUser = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(userRoles)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Guardar el usuario
            User savedUser = userRepositoryPort.save(newUser);

            // Extraer nombres de roles
            Set<String> roleNames = savedUser.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            // Generar token JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", roleNames);
            extraClaims.put("email", savedUser.getEmail());

            String token = jwtService.generateToken(extraClaims, userDetails);

            // Crear respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .roles(roleNames)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al registrar el usuario");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No hay usuario autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String username = authentication.getName();

            User user = userRepositoryPort.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));

            // Extraer nombres de roles
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            UserInfoResponse response = UserInfoResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roleNames)
                    .enabled(user.isEnabled())
                    .createdAt(user.getCreatedAt())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener información del usuario");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
