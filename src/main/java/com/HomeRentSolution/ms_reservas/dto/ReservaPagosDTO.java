package com.HomeRentSolution.ms_reservas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaPagosDTO {

    private Long idPago;
    private Long idReserva;
    private Long idInquilino;
    private Long idPropiedad;
    private BigDecimal montoTotal;
    private Integer numeroCuotas;
    private LocalDateTime fechaVencimiento;
}
