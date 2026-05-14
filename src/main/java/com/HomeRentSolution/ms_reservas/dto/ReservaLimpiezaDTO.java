package com.HomeRentSolution.ms_reservas.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaLimpiezaDTO {

    private Long idLimpieza;
    private Long idPropiedad;
    private Long idReserva;
    private LocalDateTime fechaProgramada;
    private String estadoLimpieza;

}
