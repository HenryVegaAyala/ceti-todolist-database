package com.mvc.todolist.infrastructure.dto.todo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTodoRequest {

    @NotBlank(message = "El titulo no puede estar vacío")
    private String title;

    private String description;

    public CreateTodoRequest() {
    }

    public CreateTodoRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
