package com.HomeRentSolution.ms_reservas.exception;

import com.HomeRentSolution.ms_reservas.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("[EXCEPTION] RecursoNoEncontradoException: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(PropiedadNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleNoDisponible(PropiedadNoDisponibleException ex) {
        log.warn("[EXCEPTION] PropiedadNoDisponibleException: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Conflicto de estado", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Error de validación", mensajes);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleEstadoInvalido(IllegalStateException ex) {
        log.warn("[EXCEPTION] Estado incompatible: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Estado incompatible", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("[EXCEPTION] Error inesperado: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor",
                "Ocurrió un error inesperado. Intente más tarde.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String mensaje) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(status.value(), error, mensaje, LocalDateTime.now()));
    }
}
