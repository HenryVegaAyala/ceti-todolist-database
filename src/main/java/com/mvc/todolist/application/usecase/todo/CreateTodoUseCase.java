package com.mvc.todolist.application.usecase.todo;

import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public Todo execute(String title, String description) {
        Todo todo = Todo.builder()
                .title(title)
                .description(description)
                .completed(false)
                .build();

        return todoRepositoryPort.save(todo);
    }
}
