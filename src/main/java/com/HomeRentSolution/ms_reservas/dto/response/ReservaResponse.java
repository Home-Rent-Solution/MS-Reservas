package com.HomeRentSolution.ms_reservas.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaResponse {

    private Long idReserva;

    private Long idPropiedad;

    private Long idInquilino;

    private String nombreInquilino;

    private String tituloPropiedad;

    private String ubicacion;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private BigDecimal montoTotal;

    private String estado;

    private LocalDateTime fechaReserva;

    private LocalDateTime fechaLimitePago;
}
