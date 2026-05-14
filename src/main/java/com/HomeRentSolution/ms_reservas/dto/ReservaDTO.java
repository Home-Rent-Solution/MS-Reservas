package com.HomeRentSolution.ms_reservas.dto;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaDTO {

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
