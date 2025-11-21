package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import com.mvc.todolist.infrastructure.dto.auth.AuthResponse;
import com.mvc.todolist.infrastructure.dto.auth.LoginRequest;
import com.mvc.todolist.infrastructure.dto.auth.RegisterRequest;
import com.mvc.todolist.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
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

            // Generar token JWT
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", user.getRoles());
            extraClaims.put("email", user.getEmail());

            String token = jwtService.generateToken(extraClaims, userDetails);

            // Crear respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(user.getRoles())
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

            // Crear el nuevo usuario
            User newUser = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(request.getRoles() != null && !request.getRoles().isEmpty()
                            ? request.getRoles()
                            : Set.of("ROLE_USER"))
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Guardar el usuario
            User savedUser = userRepositoryPort.save(newUser);

            // Generar token JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", savedUser.getRoles());
            extraClaims.put("email", savedUser.getEmail());

            String token = jwtService.generateToken(extraClaims, userDetails);

            // Crear respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .roles(savedUser.getRoles())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al registrar el usuario");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
