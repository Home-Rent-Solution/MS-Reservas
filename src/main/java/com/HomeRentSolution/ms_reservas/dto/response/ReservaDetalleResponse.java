package com.HomeRentSolution.ms_reservas.dto.response;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaDetalleResponse {

    private Long idReserva;

    private Long idPropiedad;

    private Long idInquilino;

    private EstadoReserva estado;

    private LocalDateTime fechaReserva;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private LocalDateTime fechaLimitesPago;

    private BigDecimal montoTotal;

    private BigDecimal montoAnticipo;

    private Integer numeroCuotas;
}
