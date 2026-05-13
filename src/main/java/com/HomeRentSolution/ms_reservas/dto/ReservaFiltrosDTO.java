package com.HomeRentSolution.ms_reservas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservaFiltrosDTO {

    private String ubicacion;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private BigDecimal precioMin;

    private BigDecimal precioMax;
}
