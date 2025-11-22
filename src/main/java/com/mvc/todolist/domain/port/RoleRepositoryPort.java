package com.mvc.todolist.domain.port;

import com.mvc.todolist.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByName(String name);

    Role save(Role role);

    Optional<Role> findById(Long id);
}

