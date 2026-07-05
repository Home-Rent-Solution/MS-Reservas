package com.HomeRentSolution.ms_reservas.dto.response;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaResponse extends RepresentationModel<ReservaResponse> {

    private Long idReserva;
    private Long idPropiedad;
    private Long idInquilino;
    private EstadoReserva estado;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaLimitesPago;
    private BigDecimal montoTotal;
}
