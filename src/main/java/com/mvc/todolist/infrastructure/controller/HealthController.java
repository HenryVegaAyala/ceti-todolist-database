package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.infrastructure.dto.health.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${aplication.version}")
    private String applicationVersion;

    @GetMapping
    public ResponseEntity<HealthCheckResponse> health() {
        boolean isDatabaseUp = checkDatabase();

        HealthCheckResponse response = HealthCheckResponse.builder()
                .status(isDatabaseUp ? "UP" : "DOWN")
                .timestamp(LocalDateTime.now())
                .application(applicationName)
                .version(applicationVersion)
                .checks(buildHealthChecks(isDatabaseUp))
                .build();

        return ResponseEntity.status(isDatabaseUp ? OK : SERVICE_UNAVAILABLE).body(response);
    }

    private boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> buildHealthChecks(boolean isDatabaseUp) {
        Runtime runtime = Runtime.getRuntime();
        long toMB = 1024 * 1024;

        return Map.of(
                "database", Map.of(
                        "status", isDatabaseUp ? "UP" : "DOWN",
                        "type", "Oracle Database"
                ),
                "application", Map.of(
                        "status", "UP",
                        "message", "Application is running"
                ),
                "memory", Map.of(
                        "status", "UP",
                        "max", runtime.maxMemory() / toMB + " MB",
                        "total", runtime.totalMemory() / toMB + " MB",
                        "used", (runtime.totalMemory() - runtime.freeMemory()) / toMB + " MB",
                        "free", runtime.freeMemory() / toMB + " MB"
                )
        );
    }
}

