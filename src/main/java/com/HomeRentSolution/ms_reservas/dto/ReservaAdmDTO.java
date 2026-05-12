package com.HomeRentSolution.ms_reservas.dto;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaAdmDTO {

    private Long id;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoReserva estado;
    private Long idInquilino;
    private String idPropiedad;
}
