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
class GobalExceptionHandlerTest {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("[EXCEPTION] RecursoNoEncontradoException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PropiedadNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handlePropiedadNoDisponible(PropiedadNoDisponibleException ex) {
        log.warn("[EXCEPTION] PropiedadNoDisponibleException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                "Propiedad no disponible", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[EXCEPTION] Validación fallida: {}", mensajes);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Error de validación", mensajes, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        log.warn("[EXCEPTION] RuntimeException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Solicitud inválida", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("[EXCEPTION] Error inesperado: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Ocurrió un error inesperado. Intente más tarde.", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}


