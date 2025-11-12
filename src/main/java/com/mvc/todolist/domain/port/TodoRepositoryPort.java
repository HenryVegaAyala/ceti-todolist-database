package com.mvc.todolist.domain.port;

import com.mvc.todolist.domain.model.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepositoryPort {

    Todo save(Todo todo);

    Optional<Todo> findById(Long id);

    List<Todo> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
