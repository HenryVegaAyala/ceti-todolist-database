package com.mvc.todolist.application.usecase.todo;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public CreateTodoUseCase(TodoRepositoryPort todoRepositoryPort) {
        this.todoRepositoryPort = todoRepositoryPort;
    }

    public Todo execute(String title, String description) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(false);

        return todoRepositoryPort.save(todo);
    }
}
