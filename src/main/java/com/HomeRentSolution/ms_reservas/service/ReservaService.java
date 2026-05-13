package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.InquilinoClient;
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
import org.springframework.web.bind.annotation.PathVariable;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final PropiedadesClient propiedadClient;
    private final InquilinoClient inquilinosClient;
    private final PagosClient pagosClient;

    public List<ReservaPropiedadDTO> buscarDisponibles(
            LocalDate inicio,
            LocalDate fin,
            String ubicacion
    ) { return buscarDisponibles();}

    public ReservaClienteDTO crearReserva(ReservaClienteDTO nuevaReserva){
        ReservaPropiedadDTO propiedadPorId = propiedadClient.obtenerPropiedadPorId(nuevaReserva.getIdReserva());
        return nuevaReserva;
    }
    //buscarDisponibilidad() *
    //crearReserva *
    //private boolean validarDisponibilidad()
    //private obtenerPrecios()
    //private void generarPago()
    //public void cancelarReserva()
    //public void confirmarReserva()
    //public obtenerReservasCliente()
    //private void enviarConfirmacion()
}

