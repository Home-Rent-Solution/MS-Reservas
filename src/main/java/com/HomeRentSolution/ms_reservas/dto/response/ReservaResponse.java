package com.HomeRentSolution.ms_reservas.dto.response;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaResponse {

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
