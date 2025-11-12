package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.application.usecase.*;
import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.infrastructure.dto.CreateTodoRequest;
import com.mvc.todolist.infrastructure.dto.TodoResponse;
import com.mvc.todolist.infrastructure.dto.UpdateTodoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final CreateTodoUserCase createTodoUserCase;
    private final UpdateTodoUserCase updateTodoUserCase;
    private final DeleteTodoUserCase deleteTodoUserCase;
    private final GetTodoByIdUseCase getTodoByIdUseCase;
    private final GetAllTodosUseCase getAllTodosUseCase;

    public TodoController(CreateTodoUserCase createTodoUserCase, UpdateTodoUserCase updateTodoUserCase, DeleteTodoUserCase deleteTodoUserCase, GetTodoByIdUseCase getTodoByIdUseCase, GetAllTodosUseCase getAllTodosUseCase) {
        this.createTodoUserCase = createTodoUserCase;
        this.updateTodoUserCase = updateTodoUserCase;
        this.deleteTodoUserCase = deleteTodoUserCase;
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
        Todo todo = createTodoUserCase.execute(request.getTitle(), request.getDescription());

        return ResponseEntity.status(HttpStatus.CREATED).body(TodoResponse.fromDomain(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        try {
            Todo todo = updateTodoUserCase.execute(id, request.getTitle(), request.getDescription(), request.getCompleted());

            return ResponseEntity.ok(TodoResponse.fromDomain(todo));

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TodoResponse> deleteTodo(@PathVariable Long id) {
        try {
            deleteTodoUserCase.execute(id);

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
