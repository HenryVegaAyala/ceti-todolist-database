package com.mvc.todolist.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateTodoRequest {
    private String title;
    private String description;
    private Boolean completed;

    public UpdateTodoRequest() {

    }

    public UpdateTodoRequest(String title, String description, Boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
}
