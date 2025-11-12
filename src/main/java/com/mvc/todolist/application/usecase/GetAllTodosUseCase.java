package com.mvc.todolist.application.usecase;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllTodosUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public GetAllTodosUseCase(TodoRepositoryPort todoRepositoryPort) {
        this.todoRepositoryPort = todoRepositoryPort;
    }

    public List<Todo> execute() {
        return todoRepositoryPort.findAll();
    }
}

