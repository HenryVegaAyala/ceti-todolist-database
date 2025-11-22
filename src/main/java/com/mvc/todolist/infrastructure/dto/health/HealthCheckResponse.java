package com.mvc.todolist.infrastructure.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResponse {
    private String status;
    private LocalDateTime timestamp;
    private String application;
    private String version;
    private Map<String, Object> checks;
}

