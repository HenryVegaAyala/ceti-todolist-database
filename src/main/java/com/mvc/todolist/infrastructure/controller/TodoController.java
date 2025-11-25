package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.application.usecase.todo.*;
import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.infrastructure.dto.todo.CreateTodoRequest;
import com.mvc.todolist.infrastructure.dto.todo.TodoResponse;
import com.mvc.todolist.infrastructure.dto.todo.UpdateTodoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final CreateTodoUseCase createTodoUseCase;
    private final UpdateTodoUseCase updateTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;
    private final GetTodoByIdUseCase getTodoByIdUseCase;
    private final GetAllTodosUseCase getAllTodosUseCase;

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        return ResponseEntity.ok(
                getAllTodosUseCase.execute()
                        .stream()
                        .map(TodoResponse::fromDomain)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@RequestBody CreateTodoRequest request) {
        Todo todo = createTodoUseCase.execute(request.getTitle(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(TodoResponse.fromDomain(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        Todo todo = updateTodoUseCase.execute(id, request.getTitle(), request.getDescription(), request.getCompleted());
        return ResponseEntity.ok(TodoResponse.fromDomain(todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        deleteTodoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        return getTodoByIdUseCase.execute(id)
                .map(todo -> ResponseEntity.ok(TodoResponse.fromDomain(todo)))
                .orElse(ResponseEntity.notFound().build());
    }
}
