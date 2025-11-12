package com.mvc.todolist.infrastructure.adapter;

import com.mvc.todolist.domain.model.Todo;

public class TodoMapper {

    public static Todo toDomain(TodoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Todo(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static TodoEntity toEntity(Todo domain) {
        if (domain == null) {
            return null;
        }

        return new TodoEntity(
                domain.getId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.isCompleted(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
