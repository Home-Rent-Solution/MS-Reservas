package com.HomeRentSolution.ms_reservas.dto.ms;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaEventoDTO {
    private Long idReserva;
    private Long idPropiedad;
    private Long idInquilino;
    private BigDecimal montoTotal;
    private BigDecimal montoReembolso;     // lo usa Pagos al cancelar
    private LocalDateTime fechaVencimiento; // lo usan ambos (fechaLimitesPago de la reserva)
    private LocalDateTime fechaFin;         // lo usa Limpieza para agendar
    private String estadoReserva;           // lo usa Limpieza
    private String motivo;
}
