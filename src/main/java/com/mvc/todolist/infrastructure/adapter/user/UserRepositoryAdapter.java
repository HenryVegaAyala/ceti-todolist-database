package com.mvc.todolist.infrastructure.adapter.user;

import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import com.mvc.todolist.infrastructure.adapter.role.RoleEntity;
import com.mvc.todolist.infrastructure.adapter.role.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper userMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, RoleJpaRepository roleJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);

        // Si el usuario tiene roles, obtenerlos desde la base de datos
        // para asegurar que son entidades administradas
        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            Set<RoleEntity> managedRoles = new HashSet<>();
            for (RoleEntity role : entity.getRoles()) {
                RoleEntity managedRole = null;

                // Buscar primero por ID si existe
                if (role.getId() != null) {
                    managedRole = roleJpaRepository.findById(role.getId()).orElse(null);
                }

                // Si no se encuentra por ID, intentar por nombre
                if (managedRole == null && role.getName() != null) {
                    managedRole = roleJpaRepository.findByName(role.getName()).orElse(null);
                }

                // Agregar el rol administrado si se encontró
                if (managedRole != null) {
                    managedRoles.add(managedRole);
                }
            }

            // Reemplazar los roles con las entidades administradas
            entity.setRoles(managedRoles);
        }

        UserEntity savedEntity = userJpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {

        Optional<UserEntity> entity = userJpaRepository.findByUsernameWithRoles(username);

        return entity.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(userMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }
}
