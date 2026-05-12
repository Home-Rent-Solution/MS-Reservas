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
public class DataInitializar implements CommandLineRunner {

    private final ReservaRepository repository;

    @Override
    public void run(String... args){
        if (repository.count() > 0){
            log.info(">>> Reservas ya cargadas. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando Reservas iniciales...");
        repository.save(new Reserva(10256,
                1222,
                EstadoReserva.PENDIENTE,
                LocalDateTime.of(2026,12,20,07,00),
                LocalDateTime.of(2027,01,20,6,30),
                LocalDateTime.of(2027,01,30,23,00),
                102);
        log.info(">>> 3 reservas cargadas OK.");
    }


}
