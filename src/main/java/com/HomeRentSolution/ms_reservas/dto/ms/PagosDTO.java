package com.HomeRentSolution.ms_reservas.dto.ms;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagosDTO {

    private Long idPago;
    private Long idReserva;
    private Long idInquilino;
    private Long idPropiedad;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private Integer numeroCuotas;
    private BigDecimal montoAnticipo;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaVencimiento;
    private String estaPago;
}
