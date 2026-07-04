package com.HomeRentSolution.ms_reservas.assemblers;

import com.HomeRentSolution.ms_reservas.controller.ReservaV2Controller;
import com.HomeRentSolution.ms_reservas.dto.ms.LimpiezaDTO;
import com.HomeRentSolution.ms_reservas.dto.ms.PagosDTO;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.HomeRentSolution.ms_reservas.client.LimpiezaClient;

@Component
public class ReservaAssemblers {

    LimpiezaClient limpiezaClient;

    public ReservaAssemblers (){
        super(ReservaV2Controller.class, ReservaResponse.class);

    }


    private ReservaResponse toResponse(Reserva reserva) {
        ReservaResponse r = new ReservaResponse();
        r.setIdReserva(reserva.getIdReserva());
        r.setIdPropiedad(reserva.getIdPropiedad());
        r.setIdInquilino(reserva.getIdInquilino());
        r.setEstado(reserva.getEstadoReserva());
        r.setFechaReserva(reserva.getFechaReserva());
        r.setFechaInicio(reserva.getFechaInicio());
        r.setFechaFin(reserva.getFechaFin());
        r.setFechaLimitesPago(reserva.getFechaLimitesPago());
        r.setMontoTotal(reserva.getMontoTotal());

        // Enriquecer con datos de MS-Limpieza
        try {

            LimpiezaDTO limpieza = limpiezaClient.getLimpiezaPorId(reserva.getIdReserva());
            if (limpieza != null) r.setIdLimpieza(limpieza.getIdLimpieza());
        } catch (Exception e) {
            log.warn("[MS-Reservas] No se pudo obtener limpieza para reserva {}", reserva.getIdReserva());
        }

        // Enriquecer con datos de MS-Pagos
        try {
            PagosDTO pago = pagosClient.obtenerPagoPorReserva(reserva.getIdReserva());
            if (pago != null) {
                r.setMontoAnticipo(pago.getMontoAnticipo());
                r.setNumeroCuotas(pago.getNumeroCuotas());
            }
        } catch (Exception e) {
            log.warn("[MS-Reservas] No se pudo obtener pago para reserva {}", reserva.getIdReserva());
        }

        return r;
    }
}
