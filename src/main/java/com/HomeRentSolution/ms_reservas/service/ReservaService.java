package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.InquilinoClient;
import com.HomeRentSolution.ms_reservas.client.PagosClient;
import com.HomeRentSolution.ms_reservas.client.PropiedadesClient;
import com.HomeRentSolution.ms_reservas.dto.ReservaFiltrosDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaInquilinoDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPrecioDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final PropiedadesClient propiedadClient;
    private final ReservaRepository reservaRepository;
    private final InquilinoClient inquilinosClient;
    private final PagosClient pagosClient;

    private List<ReservaPropiedadDTO> buscarConFiltro(ReservaFiltrosDTO filtrosDTO) {

        List<ReservaPropiedadDTO> todasPropiedades = propiedadClient.obtenerTodas();

        return todasPropiedades.stream()
                .filter(p -> cumpleEstado(p, filtrosDTO.getEstadoPropiedad()))
                .filter(p -> cumpleUbicacion(p, filtrosDTO.getUbicacion()))
                .filter(p -> cumpleRangoPrecio(p, filtrosDTO.getPrecioMin(), filtrosDTO.getPrecioMax()))
                .filter(p -> estaDisponible(p, filtrosDTO.getFechaInicio(), filtrosDTO.getFechaFin()))
                .collect(Collectors.toList());

    }

    private boolean cumpleEstado(ReservaPropiedadDTO p, EstadoPropiedad estadoFiltro) {
        if (estadoFiltro == null) return true;
        return p.getEstadoPropiedad().equals(estadoFiltro);
    }

    private boolean cumpleUbicacion(ReservaPropiedadDTO p, String ubicacionFiltro) {
        if (ubicacionFiltro == null || ubicacionFiltro.isEmpty()) return true;
        return p.getUbicacion().equalsIgnoreCase(ubicacionFiltro);
    }

    private boolean cumpleRangoPrecio(ReservaPropiedadDTO p, BigDecimal min, BigDecimal max) {
        boolean cumpleMin = (min == null) || (p.getPrecio().compareTo(min) >= 0);
        boolean cumpleMax = (max == null) || (p.getPrecio().compareTo(max) <= 0);

        return cumpleMin && cumpleMax;
    }

    private boolean estaDisponible(ReservaPropiedadDTO p, List<Reserva> todasLasReservas, LocalDateTime inicioBusqueda, LocalDateTime finBusqueda) {

        if (inicioBusqueda == null || finBusqueda == null) return true;

        boolean ocupada = reservaRepository.existsByPropiedadIdAndFechaInicioBeforeAndFechaFinAfter(
                p.getId(),
                finBusqueda,
                inicioBusqueda
        );

        return !tieneChoque;
    }

    public ReservaInquilinoDTO crearReserva(ReservaInquilinoDTO nuevaReserva){
        ReservaPropiedadDTO propiedadPorId = propiedadClient.obtenerPropiedadPorId(nuevaReserva.getIdInquilino());
        return nuevaReserva;
    }

    private boolean validarDisponibilidad(PropiedadesClient propiedadDisponible){

    }


    //buscarDisponibilidad() *
    //crearReserva *
    //private boolean validarDisponibilidad()*
    //private obtenerPrecios()
    //private void generarPago()
    //public void cancelarReserva()
    //public void confirmarReserva()
    //public obtenerReservasCliente()
    //private void enviarConfirmacion()
}

