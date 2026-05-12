package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.dto.ReservaClienteDTO;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaClienteDTO crearReserva(Long idInquilino, Long idPropiedad,
                                          LocalDateTime inicio, LocalDateTime fin) {

        if (!disponibilidad.isDisponible()) {
            if ("CONFIRMADA".equals(disponibilidad.getRazon())) {
                throw new PropiedadOcupadaException("Propiedad ya reservada.");
            }
            if ("PENDIENTE".equals(disponibilidad.getRazon())) {
                throw new ReservaPendienteException("Reserva pendiente en curso.");
            }
        }



        Reserva reserva = new Reserva();
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setIdInquuilino(idInquilino);
        reserva.setIdPropiedad(idPropiedad);



        Reserva guardada = reservaRepository.save(reserva);

        ReservaClienteDTO dto = new ReservaClienteDTO();
        dto.setId(guardada.getId());
        dto.setFechaInicio(guardada.getFechaInicio());
        dto.setFechaFin(guardada.getFechaFin());
        dto.setEstado(guardada.getEstado());
        dto.setIdPropiedad(guardada.getIdPropiedad());
        return dto;
    }


}
