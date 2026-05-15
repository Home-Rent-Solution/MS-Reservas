package com.HomeRentSolution.ms_reservas.config;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;

import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ReservaRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            log.info(">>> Reservas ya cargadas. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando Reservas iniciales...");

        // Reserva 1
        Reserva reserva1 = new Reserva();
        reserva1.setIdReserva(10256L);
        reserva1.setIdPropiedad(1222L);
        reserva1.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva1.setFechaReserva(LocalDateTime.of(2026, 12, 20, 7, 0));
        reserva1.setFechaLimitesPago(LocalDateTime.of(2027, 1, 20, 6, 30));
        reserva1.setFechaFin(LocalDateTime.of(2027, 1, 30, 23, 0));
        reserva1.setIdInquilino(102L);
        repository.save(reserva1);

        // Reserva 2
        Reserva reserva2 = new Reserva();
        reserva2.setIdReserva(10257L);
        reserva2.setIdPropiedad(1223L);
        reserva2.setEstadoReserva(EstadoReserva.CONFIRMADA); // ← CORREGIDO
        reserva2.setFechaReserva(LocalDateTime.of(2026, 12, 21, 8, 0));
        reserva2.setFechaLimitesPago(LocalDateTime.of(2027, 1, 21, 7, 30));
        reserva2.setFechaFin(LocalDateTime.of(2027, 1, 31, 22, 0));
        reserva2.setIdInquilino(103L);
        repository.save(reserva2);

        // Reserva 3
        Reserva reserva3 = new Reserva();
        reserva3.setIdReserva(10258L);
        reserva3.setIdPropiedad(1224L);
        reserva3.setEstadoReserva(EstadoReserva.CANCELADA);
        reserva3.setFechaReserva(LocalDateTime.of(2026, 12, 22, 9, 0));
        reserva3.setFechaLimitesPago(LocalDateTime.of(2027, 1, 22, 8, 30));
        reserva3.setFechaFin(LocalDateTime.of(2027, 1, 31, 21, 0));
        reserva3.setIdInquilino(104L);
        repository.save(reserva3);

        log.info(">>> 3 reservas cargadas OK.");
    }


}
