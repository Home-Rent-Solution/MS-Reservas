package com.HomeRentSolution.ms_reservas.dto.response;

import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaDetalleResponse {

    // De la reserva local
    private Long idReserva;
    private EstadoReserva estadoReserva;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaLimitesPago;
    private BigDecimal montoTotal;

    // De MS-Propiedades
    private Long idPropiedad;
    private String ubicacion;
    private EstadoPropiedad estadoPropiedad;
    private BigDecimal precioBase;

    // De MS-Precios
    private String temporada;
    private Double multiplicador;

    // De MS-Inquilinos
    private Long idInquilino;
    private String nombre;
    private String email;

    // De MS-Limpieza (solo cuando aplica)
    private Long idLimpieza;
    private LocalDateTime fechaProgramada;
    private String estadoLimpieza;

}
