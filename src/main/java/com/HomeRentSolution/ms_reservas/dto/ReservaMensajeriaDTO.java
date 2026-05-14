package com.HomeRentSolution.ms_reservas.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaMensajeriaDTO {


    private Long idMensaje;
    private LocalDateTime fecha;
    private String contenido;
    private Long idEmisor;
    private Long idReceptor;

}
