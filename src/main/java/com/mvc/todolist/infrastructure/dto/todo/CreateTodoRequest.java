package com.mvc.todolist.infrastructure.dto.todo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTodoRequest {
    private String title;
    private String description;

    public CreateTodoRequest() {
    }

    public CreateTodoRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
