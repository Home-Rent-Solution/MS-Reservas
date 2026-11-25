package com.HomeRentSolution.ms_reservas.exception;

// Se responde con 403: por ejemplo, un inquilino que pide una reserva que no es suya
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
