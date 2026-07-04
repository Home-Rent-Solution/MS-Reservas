package com.HomeRentSolution.ms_reservas.dto.ms;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoEventoDTO {
    private Long idReserva;
    private Long idPropiedad;
    private Long idInquilino;
    private BigDecimal montoTotal;     // presente en pago.creado
    private BigDecimal montoReembolso; // presente en pago.eliminado
}
