package com.HomeRentSolution.ms_reservas.model;


public enum EstadoReserva {
    PENDIENTE, //Aún no se aprueba o no se paga
    COMFIRMADA, //Está aceptada y activa
    CANCELADA, //Se anuló
    FINALIZADA //La estadía ya terminó
}
