package com.mvc.todolist.application.usecase;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteTodoUserCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public DeleteTodoUserCase(TodoRepositoryPort todoRepositoryPort) {
        this.todoRepositoryPort = todoRepositoryPort;
    }

    public Todo execute(Long id) {
        if (!todoRepositoryPort.existsById(id)) {
            throw new RuntimeException("Todo not found with id: " + id);
        }

        Todo todoToDelete = todoRepositoryPort.findById(id).orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        todoRepositoryPort.deleteById(id);

        return todoToDelete;
    }
}
