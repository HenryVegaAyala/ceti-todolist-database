package com.mvc.todolist.application.usecase.todo;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GetAllTodosUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public List<Todo> execute() {
        return todoRepositoryPort.findAll();
    }
}

