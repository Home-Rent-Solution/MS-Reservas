package com.HomeRentSolution.ms_reservas.dto.ms;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LimpiezaDTO {

    private Long idLimpieza;
    private Long idPropiedad;
    private Long idReserva;
    private LocalDateTime fechaProgramada;
    private String estadoLimpieza;
    private String motivo;

}
