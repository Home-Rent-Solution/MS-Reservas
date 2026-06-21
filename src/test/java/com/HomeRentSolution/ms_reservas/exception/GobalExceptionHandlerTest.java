package com.HomeRentSolution.ms_reservas.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GobalExceptionHandlerTest {

    private final GobalExceptionHandler handler = new GobalExceptionHandler();

    @Test
    void validacionRetornaErroresPorCampo() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("reserva", "fechaInicio", "es obligatoria")
        ));

        ResponseEntity<Map<String, String>> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("es obligatoria", response.getBody().get("fechaInicio"));
    }

    @Test
    void propiedadNoDisponibleRetorna409() {
        ResponseEntity<Map<String, String>> response = handler.handlePropiedadNoDisponible(
                new PropiedadNoDisponibleException("propiedad ocupada")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("propiedad ocupada", response.getBody().get("error"));
    }

    @Test
    void recursoNoEncontradoRetorna404() {
        ResponseEntity<Map<String, String>> response = handler.handleRecursoNoEncontrado(
                new RecursoNoEncontradoException("reserva inexistente")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("reserva inexistente", response.getBody().get("error"));
    }

    @Test
    void runtimeRetorna400() {
        ResponseEntity<Map<String, String>> response =
                handler.handleRuntime(new RuntimeException("estado inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("estado inválido", response.getBody().get("error"));
    }
}
