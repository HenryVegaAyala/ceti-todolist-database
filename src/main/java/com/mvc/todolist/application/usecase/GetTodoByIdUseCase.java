package com.mvc.todolist.application.usecase;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetTodoByIdUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public GetTodoByIdUseCase(TodoRepositoryPort todoRepositoryPort) {
        this.todoRepositoryPort = todoRepositoryPort;
    }

    public Optional<Todo> execute(Long id) {
        return todoRepositoryPort.findById(id);
    }
}
