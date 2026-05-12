package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.dto.ReservaClienteDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaPropiedadDTO propiedadClient;

    public List<ReservaPropiedadDTO> buscarDisponiblesParaCalendario(LocalDateTime inicio, LocalDateTime fin) {
        // 1. Traer TODAS las propiedades del micro de tu compañero vía Feign
        List<ReservaPropiedadDTO> todas = propiedadClient.obtenerTodas();

        // 2. Obtener de TU base de datos los IDs de las propiedades ocupadas
        // Nota: Si tu DB usa Long y él Integer, solo hacemos un cast rápido
        List<Integer> idsOcupados = reservaRepository.findIdsOcupadosEnRango(inicio, fin)
                .stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        // 3. Filtrar: "Deme las que NO están ocupadas"
        return todas.stream()
                .filter(p -> !idsOcupados.contains(p.getIdPropiedad()))
                .collect(Collectors.toList());
    }

    public ReservaClienteDTO crearReserva(Long idInquilino, Long idPropiedad,
                                          LocalDateTime inicio, LocalDateTime fin) {

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
