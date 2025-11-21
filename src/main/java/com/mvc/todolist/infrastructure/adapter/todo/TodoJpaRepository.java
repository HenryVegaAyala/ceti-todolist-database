package com.mvc.todolist.infrastructure.adapter.todo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoJpaRepository extends JpaRepository<TodoEntity, Long> {
}
