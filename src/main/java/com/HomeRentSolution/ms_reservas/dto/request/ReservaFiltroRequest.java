package com.HomeRentSolution.ms_reservas.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaFiltroRequest {

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String ubicacion;
    private BigDecimal precioMin;
    private BigDecimal precioMax;

}
