package com.mvc.todolist.infrastructure.adapter.user;
import com.mvc.todolist.domain.model.Role;
import org.springframework.stereotype.Component;
@Component
public class RoleMapper {
    public Role toDomain(RoleEntity roleEntity) {
        if (roleEntity == null) {
            return null;
        }
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .createdAt(roleEntity.getCreatedAt())
                .updatedAt(roleEntity.getUpdatedAt())
                .build();
    }
    public RoleEntity toEntity(Role role) {
        if (role == null) {
            return null;
        }
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
