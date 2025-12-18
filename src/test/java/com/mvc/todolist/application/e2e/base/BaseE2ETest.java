package com.mvc.todolist.application.e2e.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.todolist.domain.model.Role;
import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.RoleRepositoryPort;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import com.mvc.todolist.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseE2ETest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepositoryPort userRepositoryPort;

    @Autowired
    protected RoleRepositoryPort roleRepositoryPort;

    @Autowired
    protected TodoRepositoryPort todoRepositoryPort;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    protected Role roleUser;
    protected Role roleAdmin;
    protected User testUser;
    protected User testAdmin;
    protected String userToken;
    protected String adminToken;


    @BeforeEach
    public void baseSetup() {
        initializeRoles();
        initializeUsers();
        generateTokens();
    }

    protected void initializeRoles() {
        roleUser = roleRepositoryPort.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("ROLE_USER")
                            .description("Usuario regular")
                            .build();

                    return roleRepositoryPort.save(role);
                });

        roleAdmin = roleRepositoryPort.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("ROLE_ADMIN")
                            .description("Administrador")
                            .build();

                    return roleRepositoryPort.save(role);
                });
    }

    protected void initializeUsers() {
        if (userRepositoryPort.findByUsername("testuser").isEmpty()) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleUser);

            testUser = User.builder()
                    .username("testuser")
                    .email("testuser@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .roles(userRoles)
                    .build();

            testUser = userRepositoryPort.save(testUser);
        } else {
            testUser = userRepositoryPort.findByUsername("testuser").get();
        }

        if (userRepositoryPort.findByUsername("testadmin").isEmpty()) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleUser);
            adminRoles.add(roleAdmin);

            testAdmin = User.builder()
                    .username("testadmin")
                    .email("testadmin@test.com")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .roles(adminRoles)
                    .build();

            testAdmin = userRepositoryPort.save(testAdmin);
        } else {
            testAdmin = userRepositoryPort.findByUsername("testadmin").get();
        }
    }

    protected void generateTokens() {

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getUsername())
                .password(testUser.getPassword())
                .roles("USER")
                .build();

        userToken = jwtService.generateToken(userDetails);

        UserDetails adminDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getUsername())
                .password(testUser.getPassword())
                .roles("USER", "ADMIN")
                .build();

        adminToken = jwtService.generateToken(adminDetails);
    }

    protected User createTempUser(String username, String email, Set<Role> roles) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("temp123"))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .roles(roles)
                .build();

        return userRepositoryPort.save(user);
    }

    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    protected String bearerToken(String token) {
        return "Bearer " + token;
    }
}
