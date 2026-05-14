package com.HomeRentSolution.ms_reservas.dto;

import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaFiltrosDTO {

    private Long idPropiedad;

    private String ubicacion;

    private EstadoPropiedad estadoPropiedad;

    private EstadoReserva estadoReserva;

    private BigDecimal precioMin;

    private BigDecimal precioMax;

    private LocalDateTime fechaReserva;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private LocalDateTime fechaLimitePago;

}
