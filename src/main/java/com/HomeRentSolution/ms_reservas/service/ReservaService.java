package com.HomeRentSolution.ms_reservas.service;

import com.HomeRentSolution.ms_reservas.client.InquilinoClient;
import com.HomeRentSolution.ms_reservas.client.PagosClient;
import com.HomeRentSolution.ms_reservas.client.PropiedadesClient;
import com.HomeRentSolution.ms_reservas.dto.ReservaInquilinoDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPrecioDTO;
import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservaService {

    private final PropiedadesClient propiedadClient;
    private final ReservaRepository reservaRepository;
    private final InquilinoClient inquilinosClient;
    private final PagosClient pagosClient;

    private List<ReservaPropiedadDTO>
            propiedadesDisponibles = propiedadClient.obtenerTodas(){

    }

    public ReservaInquilinoDTO crearReserva(ReservaInquilinoDTO nuevaReserva){
        ReservaPropiedadDTO propiedadPorId = propiedadClient.obtenerPropiedadPorId(nuevaReserva.getIdInquilino());
        return nuevaReserva;
    }

    private boolean validarDisponibilidad(PropiedadesClient propiedadDisponible)


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

