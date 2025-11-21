package com.mvc.todolist.infrastructure.adapter.todo;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TodoRepositoryAdapter implements TodoRepositoryPort {

    private final TodoJpaRepository todoJpaRepository;

    public TodoRepositoryAdapter(TodoJpaRepository todoJpaRepository) {
        this.todoJpaRepository = todoJpaRepository;
    }

    @Override
    public Todo save(Todo todo) {
        TodoEntity entity = TodoMapper.toEntity(todo);
        TodoEntity savedEntity = todoJpaRepository.save(entity);

        return TodoMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoJpaRepository.findById(id).map(TodoMapper::toDomain);
    }

    @Override
    public List<Todo> findAll() {
        return todoJpaRepository.findAll()
                .stream()
                .map(TodoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        todoJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return todoJpaRepository.existsById(id);
    }
}
