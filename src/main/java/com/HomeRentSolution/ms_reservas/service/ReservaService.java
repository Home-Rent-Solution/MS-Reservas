package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.PagosClient;
import com.HomeRentSolution.ms_reservas.client.PropiedadesClient;
import com.HomeRentSolution.ms_reservas.dto.ReservaClienteDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PropiedadesClient propiedadClient;
    private final PagosClient pagosClient;

    public Reserva crearReserva(Reserva nuevaReserva) {
        ReservaPropiedadDTO propiedad = propiedadClient.obtenerPropiedadPorId(nuevaReserva.getIdPropiedad());


        if (!propiedad.isDisponible()) {
            throw new RuntimeException("La propiedad no está disponible");
        }

        // 2. Calcular monto total (precio * días)
        long dias = ChronoUnit.DAYS.between(
                nuevaReserva.getFechaInicio(), nuevaReserva.getFechaFin()
        );
        nuevaReserva.setMontoTotal(propiedad.getPrecio().multiply(BigDecimal.valueOf(dias)));

        nuevaReserva.setFechaReserva(LocalDateTime.now());
        nuevaReserva.setFechaLimitesPago(LocalDateTime.now().plusHours(1));
        nuevaReserva.setEstado(EstadoReserva.PENDIENTE);

        try {
            propiedadClient.cambiarEstado(nuevaReserva.getIdPropiedad());
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el microservicio de propiedades: " + e.getMessage());
        }

        return reservaRepository.save(nuevaReserva);
    }

    @Scheduled(fixedRate = 60000)
    public void cancelarReservasVencidas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Reserva> vencidas = reservaRepository.findByEstadoAndFechaLimitePagoBefore(
                        EstadoReserva.PENDIENTE, ahora
                );

        vencidas.forEach(reserva -> {
            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaRepository.save(reserva);


            propiedadClient.cambiarEstado(reserva.getIdPropiedad());
        });
    }
}
