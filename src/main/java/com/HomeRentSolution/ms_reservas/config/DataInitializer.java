package com.HomeRentSolution.ms_reservas.config;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ReservaRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info(">>> Reservas ya cargadas. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando reservas iniciales...");

        // Reserva 1 — PENDIENTE
        Reserva r1 = new Reserva();
        r1.setIdPropiedad(1222L);
        r1.setIdInquilino(102L);
        r1.setEstadoReserva(EstadoReserva.PENDIENTE);
        r1.setFechaReserva(LocalDateTime.now());
        r1.setFechaInicio(LocalDateTime.of(2026, 12, 20, 14, 0));
        r1.setFechaFin(LocalDateTime.of(2027,  1, 20, 11, 0));
        r1.setFechaLimitesPago(LocalDateTime.now().plusDays(3));
        r1.setMontoTotal(new BigDecimal("500000"));
        repository.save(r1);

        // Reserva 2 — CONFIRMADA
        Reserva r2 = new Reserva();
        r2.setIdPropiedad(1223L);
        r2.setIdInquilino(103L);
        r2.setEstadoReserva(EstadoReserva.CONFIRMADA);
        r2.setFechaReserva(LocalDateTime.now().minusDays(5));
        r2.setFechaInicio(LocalDateTime.of(2026, 12, 21, 14, 0));
        r2.setFechaFin(LocalDateTime.of(2027,  1, 31, 11, 0));
        r2.setFechaLimitesPago(LocalDateTime.now().minusDays(2));
        r2.setMontoTotal(new BigDecimal("750000"));
        repository.save(r2);

        // Reserva 3 — CANCELADA
        Reserva r3 = new Reserva();
        r3.setIdPropiedad(1224L);
        r3.setIdInquilino(104L);
        r3.setEstadoReserva(EstadoReserva.CANCELADA);
        r3.setFechaReserva(LocalDateTime.now().minusDays(10));
        r3.setFechaInicio(LocalDateTime.of(2026, 12, 22, 14, 0));
        r3.setFechaFin(LocalDateTime.of(2027,  1, 31, 11, 0));
        r3.setFechaLimitesPago(LocalDateTime.now().minusDays(7));
        r3.setMontoTotal(new BigDecimal("300000"));
        repository.save(r3);

        log.info(">>> 3 reservas cargadas OK.");
    }

}
