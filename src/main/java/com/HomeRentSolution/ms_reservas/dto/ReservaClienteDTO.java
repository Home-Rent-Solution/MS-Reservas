package com.HomeRentSolution.ms_reservas.dto;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaClienteDTO {
    private Long id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoReserva estado;
    private String idPropiedad;
}
