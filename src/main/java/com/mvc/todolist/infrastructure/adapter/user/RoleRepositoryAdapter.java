package com.mvc.todolist.infrastructure.adapter.user;

import com.mvc.todolist.domain.model.Role;
import com.mvc.todolist.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository, RoleMapper roleMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name)
                .map(roleMapper::toDomain);
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = roleMapper.toEntity(role);
        RoleEntity savedEntity = roleJpaRepository.save(entity);
        return roleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id)
                .map(roleMapper::toDomain);
    }
}

