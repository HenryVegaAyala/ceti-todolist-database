package com.mvc.todolist.infrastructure.controller;

import com.mvc.todolist.infrastructure.dto.health.HealthCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;
    private final String applicationName;
    private final String applicationVersion;

    public HealthController(DataSource dataSource,
                            @Autowired(required = false) HealthEndpoint healthEndpoint,
                            @Value("${spring.application.name}") String applicationName,
                            @Value("${aplication.version}") String applicationVersion) {
        this.dataSource = dataSource;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @GetMapping
    public ResponseEntity<HealthCheckResponse> health() {
        boolean isDatabaseUp = checkDatabase();
        String status = isDatabaseUp ? "UP" : "DOWN";
        HttpStatus httpStatus = isDatabaseUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        HealthCheckResponse response = HealthCheckResponse.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .application(applicationName)
                .version(applicationVersion)
                .checks(getHealthChecks(isDatabaseUp))
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }

    private boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> getHealthChecks(boolean isDatabaseUp) {
        Map<String, Object> checks = new HashMap<>();

        // Database check
        Map<String, String> databaseCheck = new HashMap<>();
        databaseCheck.put("status", isDatabaseUp ? "UP" : "DOWN");
        databaseCheck.put("type", "Oracle Database");
        checks.put("database", databaseCheck);

        // Application check
        Map<String, String> appCheck = new HashMap<>();
        appCheck.put("status", "UP");
        appCheck.put("message", "Application is running");
        checks.put("application", appCheck);

        // Memory check
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        Map<String, Object> memoryCheck = new HashMap<>();
        memoryCheck.put("status", "UP");
        memoryCheck.put("max", maxMemory + " MB");
        memoryCheck.put("total", totalMemory + " MB");
        memoryCheck.put("used", usedMemory + " MB");
        memoryCheck.put("free", freeMemory + " MB");
        checks.put("memory", memoryCheck);

        return checks;
    }
}

