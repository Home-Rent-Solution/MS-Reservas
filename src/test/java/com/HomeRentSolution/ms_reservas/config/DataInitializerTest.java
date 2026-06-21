package com.HomeRentSolution.ms_reservas.config;

import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private ReservaRepository repository;

    @Test
    void noCargaDatosCuandoYaExistenReservas() {
        when(repository.count()).thenReturn(1L);

        new DataInitializer(repository).run();

        verify(repository, never()).save(any());
    }

    @Test
    void cargaTresReservasCuandoLaBaseEstaVacia() {
        when(repository.count()).thenReturn(0L);
        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);

        new DataInitializer(repository).run();

        verify(repository, times(3)).save(captor.capture());
        List<Reserva> reservas = captor.getAllValues();
        assertEquals(3, reservas.size());
        assertTrue(reservas.stream().allMatch(r -> r.getIdPropiedad() != null));
        assertTrue(reservas.stream().allMatch(r -> r.getIdInquilino() != null));
        assertTrue(reservas.stream().allMatch(r -> r.getMontoTotal() != null));
        assertTrue(reservas.stream().allMatch(r -> r.getEstadoReserva() != null));
    }
}
