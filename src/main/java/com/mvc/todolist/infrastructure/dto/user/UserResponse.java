package com.mvc.todolist.infrastructure.dto.user;

import com.mvc.todolist.domain.model.Role;
import com.mvc.todolist.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles().stream().map(Role::getName).collect(toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

