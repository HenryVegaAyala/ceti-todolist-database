package com.mvc.todolist.application.e2e.base;


import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTodoE2ETest extends BaseE2ETest {

    @Autowired
    protected TodoRepositoryPort todoRepositoryPort;

    protected List<Todo> testTodos = new ArrayList<>();

    protected Todo createTestTodo(String title, String description, boolean completed) {
        Todo todo = Todo.builder()
                .title(title)
                .description(description)
                .completed(completed)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return todoRepositoryPort.save(todo);
    }

    protected Todo createTestTodo(String title) {
        return createTestTodo(title, "Descripción de " + title, false);
    }

    protected List<Todo> createMultipleTodos(int count) {
        List<Todo> todos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Todo todo = createTestTodo(
                    "Tarea " + i,
                    "Descripción de la tarea " + i,
                    i % 2 == 0
            );
            todos.add(todo);
        }
        return todos;
    }

    protected void cleanupTestTodos() {
        testTodos.forEach(todo -> todoRepositoryPort.deleteById(todo.getId()));
        testTodos.clear();
    }

    protected boolean todoExists(Long todoId) {
        return todoRepositoryPort.findById(todoId).isPresent();
    }
}
