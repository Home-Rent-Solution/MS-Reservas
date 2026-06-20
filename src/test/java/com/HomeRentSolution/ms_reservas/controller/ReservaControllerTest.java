package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.dto.request.ReservaCancelarRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaControllerTest {

    @Mock
    private ReservaService reservasService;

    @InjectMocks
    private ReservaController reservaController;

    private Reserva reserva;
    private ReservaResponse reservaResponse;

    @BeforeEach
    void setUp() {
        reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setIdPropiedad(10L);
        reserva.setIdInquilino(20L);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaInicio(LocalDateTime.now().plusDays(5));
        reserva.setFechaFin(LocalDateTime.now().plusDays(10));
        reserva.setFechaLimitesPago(LocalDateTime.now().plusDays(3));
        reserva.setMontoTotal(new BigDecimal("300000"));

        reservaResponse = new ReservaResponse();
        reservaResponse.setIdReserva(1L);
        reservaResponse.setIdPropiedad(10L);
        reservaResponse.setIdInquilino(20L);
        reservaResponse.setEstado(EstadoReserva.PENDIENTE);
        reservaResponse.setMontoTotal(new BigDecimal("300000"));
    }

    // PRUEBA 1: obtener por ID devuelve 200 OK
    @Test
    void obtenerPorId_debeRetornar200() {
        when(reservasService.buscarPorId(1L)).thenReturn(reserva);

        ResponseEntity<Reserva> respuesta = reservaController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdReserva());
    }

    // PRUEBA 2: obtener todas devuelve lista
    @Test
    void obtenerTodas_debeRetornarLista() {
        when(reservasService.buscarTodas()).thenReturn(List.of(reserva));

        ResponseEntity<List<Reserva>> respuesta = reservaController.obtenerTodas();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertFalse(respuesta.getBody().isEmpty());
    }

    // PRUEBA 3: crear reserva devuelve 201 CREATED
    @Test
    void crearReserva_debeRetornar201() {
        when(reservasService.crearReserva(any(ReservaCrearRequest.class)))
                .thenReturn(reservaResponse);

        ReservaCrearRequest request = new ReservaCrearRequest();
        ResponseEntity<ReservaResponse> respuesta = reservaController.crearReserva(request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
    }

    // PRUEBA 4: confirmar reserva devuelve 200 OK
    @Test
    void confirmarReserva_debeRetornar200() {
        when(reservasService.confirmarReserva(1L)).thenReturn(reservaResponse);

        ResponseEntity<ReservaResponse> respuesta = reservaController.confirmarReserva(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(reservasService, times(1)).confirmarReserva(1L);
    }

    // PRUEBA 5: cancelar reserva devuelve 200 OK
    @Test
    void cancelarReserva_debeRetornar200() {
        when(reservasService.cancelarReserva(anyLong(), anyString()))
                .thenReturn(reservaResponse);

        ReservaCancelarRequest req = new ReservaCancelarRequest();
        req.setMotivo("Motivo de prueba");
        ResponseEntity<ReservaResponse> respuesta = reservaController.cancelarReserva(1L, req);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(reservasService, times(1)).cancelarReserva(1L, "Motivo de prueba");
    }

    // PRUEBA 6: buscar por estado devuelve lista filtrada
    @Test
    void buscarPorEstado_debeRetornarListaFiltrada() {
        when(reservasService.buscarPorEstado(EstadoReserva.PENDIENTE))
                .thenReturn(List.of(reserva));

        ResponseEntity<List<Reserva>> respuesta =
                reservaController.buscarPorEstado("PENDIENTE");

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertFalse(respuesta.getBody().isEmpty());
    }
}
