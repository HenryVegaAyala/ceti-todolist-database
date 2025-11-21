package com.mvc.todolist.infrastructure.adapter.user;

import com.mvc.todolist.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class UserMapper {

    public User toDomain(UserEntity userEntity) {

        if (userEntity == null) {
            return null;
        }

        return User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(new HashSet<>(Arrays.asList(userEntity.getRoles().split(","))))
                .enabled(userEntity.getEnabled())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles() != null ? String.join(",", user.getRoles()) : "ROLE_USUARIO")
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
