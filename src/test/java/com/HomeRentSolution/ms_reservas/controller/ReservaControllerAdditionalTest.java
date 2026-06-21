package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltroRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaDetalleResponse;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaControllerAdditionalTest {

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController controller;

    @Test
    void obtieneVistaParaCliente() {
        ReservaResponse dto = new ReservaResponse();
        when(reservaService.obtenerParaCliente(1L)).thenReturn(dto);

        ResponseEntity<ReservaResponse> response = controller.getParaCliente(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());
    }

    @Test
    void obtieneVistaAdministrativa() {
        ReservaDetalleResponse dto = new ReservaDetalleResponse();
        when(reservaService.obtenerParaAdmin(1L)).thenReturn(dto);

        ResponseEntity<ReservaDetalleResponse> response = controller.getParaAdmin(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());
    }

    @Test
    void buscarDisponiblesConstruyeFiltroCompleto() {
        LocalDate inicio = LocalDate.of(2026, 7, 1);
        LocalDate fin = LocalDate.of(2026, 7, 5);
        BigDecimal minimo = new BigDecimal("30000");
        BigDecimal maximo = new BigDecimal("120000");
        ReservaDetalleResponse detalle = new ReservaDetalleResponse();
        when(reservaService.buscarConFiltro(any(ReservaFiltroRequest.class)))
                .thenReturn(List.of(detalle));

        ResponseEntity<List<ReservaDetalleResponse>> response =
                controller.buscarDisponibles(inicio, fin, "Santiago", minimo, maximo);

        ArgumentCaptor<ReservaFiltroRequest> captor =
                ArgumentCaptor.forClass(ReservaFiltroRequest.class);
        verify(reservaService).buscarConFiltro(captor.capture());
        ReservaFiltroRequest filtro = captor.getValue();
        assertEquals(inicio.atStartOfDay(), filtro.getFechaInicio());
        assertEquals(fin.atStartOfDay(), filtro.getFechaFin());
        assertEquals("Santiago", filtro.getUbicacion());
        assertEquals(minimo, filtro.getPrecioMin());
        assertEquals(maximo, filtro.getPrecioMax());
        assertEquals(List.of(detalle), response.getBody());
    }

    @Test
    void finalizarReservaRetornaDetalle() {
        ReservaDetalleResponse dto = new ReservaDetalleResponse();
        when(reservaService.finalizarReserva(1L)).thenReturn(dto);

        ResponseEntity<ReservaDetalleResponse> response = controller.finalizarReserva(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(dto, response.getBody());
    }
}
