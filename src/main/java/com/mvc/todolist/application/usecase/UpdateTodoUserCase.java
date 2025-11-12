package com.mvc.todolist.application.usecase;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateTodoUserCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public UpdateTodoUserCase(TodoRepositoryPort todoRepositoryPort) {
        this.todoRepositoryPort = todoRepositoryPort;
    }

    public Todo execute(Long id, String title, String description, Boolean completed) {
        Todo todo = todoRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        if (title != null) {
            todo.setTitle(title);
        }
        if (description != null) {
            todo.setDescription(description);
        }
        if (completed != null) {
            todo.setCompleted(completed);
        }

        return todoRepositoryPort.save(todo);
    }
}
