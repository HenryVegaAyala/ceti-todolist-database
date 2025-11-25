package com.mvc.todolist.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Error de validación"
                ));

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                "Los datos proporcionados no son válidos",
                request,
                errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {

        log.error("Credenciales incorrectas: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas",
                "Usuario o contraseña incorrectos",
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.error("Error de autenticación: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "No autorizado",
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        log.error("Acceso denegado: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Acceso denegado",
                "No tienes permisos para acceder a este recurso",
                request
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.error("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.error("Argumento inválido: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Argumento inválido",
                ex.getMessage(),
                request
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException ex, WebRequest request) {

        log.warn("Recurso estático no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                "El recurso solicitado no está disponible",
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Error interno del servidor: ", ex);

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ha ocurrido un error inesperado. Por favor, intente más tarde.",
                request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message, WebRequest request) {
        return buildErrorResponse(status, error, message, request, null);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message, WebRequest request, Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(extractPath(request))
                .validationErrors(validationErrors)
                .build();
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}

