package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.InquilinoClient;
import com.HomeRentSolution.ms_reservas.client.PagosClient;
import com.HomeRentSolution.ms_reservas.client.PropiedadesClient;
import com.HomeRentSolution.ms_reservas.dto.ReservaInquilinoDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
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

    public ReservaInquilinoDTO crearReserva(ReservaInquilinoDTO nuevaReserva){
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

