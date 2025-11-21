package com.mvc.todolist.application.usecase.todo;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public DeleteTodoUseCase(TodoRepositoryPort todoRepositoryPort) {
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
