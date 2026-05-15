package com.HomeRentSolution.ms_reservas.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaCrearRequest {

    private Long idPropiedad;
    private Long idInquilino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
