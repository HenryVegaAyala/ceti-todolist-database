package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.application.usecase.todo.*;
import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.infrastructure.dto.todo.CreateTodoRequest;
import com.mvc.todolist.infrastructure.dto.todo.TodoResponse;
import com.mvc.todolist.infrastructure.dto.todo.UpdateTodoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final CreateTodoUseCase createTodoUseCase;
    private final UpdateTodoUseCase updateTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;
    private final GetTodoByIdUseCase getTodoByIdUseCase;
    private final GetAllTodosUseCase getAllTodosUseCase;

    public TodoController(CreateTodoUseCase createTodoUseCase, UpdateTodoUseCase updateTodoUseCase, DeleteTodoUseCase deleteTodoUseCase, GetTodoByIdUseCase getTodoByIdUseCase, GetAllTodosUseCase getAllTodosUseCase) {
        this.createTodoUseCase = createTodoUseCase;
        this.updateTodoUseCase = updateTodoUseCase;
        this.deleteTodoUseCase = deleteTodoUseCase;
        this.getTodoByIdUseCase = getTodoByIdUseCase;
        this.getAllTodosUseCase = getAllTodosUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = getAllTodosUseCase.execute()
                .stream()
                .map(TodoResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@RequestBody CreateTodoRequest request) {
        Todo todo = createTodoUseCase.execute(request.getTitle(), request.getDescription());

        return ResponseEntity.status(HttpStatus.CREATED).body(TodoResponse.fromDomain(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        try {
            Todo todo = updateTodoUseCase.execute(id, request.getTitle(), request.getDescription(), request.getCompleted());

            return ResponseEntity.ok(TodoResponse.fromDomain(todo));

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TodoResponse> deleteTodo(@PathVariable Long id) {
        try {
            deleteTodoUseCase.execute(id);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        return getTodoByIdUseCase.execute(id)
                .map(todo -> ResponseEntity.ok(TodoResponse.fromDomain(todo)))
                .orElse(ResponseEntity.notFound().build());
    }
}
