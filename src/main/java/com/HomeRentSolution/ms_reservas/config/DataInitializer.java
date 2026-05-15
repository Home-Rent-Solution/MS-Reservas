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

        repository.save(new Reserva(10256, 1222, EstadoReserva.PENDIENTE,
                LocalDateTime.of(2026, 12, 20, 7, 00),
                LocalDateTime.of(2027, 1, 20, 6, 30),
                LocalDateTime.of(2027, 1, 30, 23, 00), 102));

        repository.save(new Reserva(10257, 1223, EstadoReserva.COMPLETADA,
                LocalDateTime.of(2026, 12, 21, 8, 00),
                LocalDateTime.of(2027, 1, 21, 7, 30),
                LocalDateTime.of(2027, 1, 31, 22, 00), 103));

        repository.save(new Reserva(10258, 1224, EstadoReserva.CANCELADA,
                LocalDateTime.of(2026, 12, 22, 9, 00),
                LocalDateTime.of(2027, 1, 22, 8, 30),
                LocalDateTime.of(2027, 1, 31, 21, 00), 104));

        log.info(">>> 3 reservas cargadas OK.");
    }


}
