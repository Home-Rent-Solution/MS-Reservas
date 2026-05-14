package com.HomeRentSolution.ms_reservas.dto;

import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaFiltrosDTO {

    private Long idReserva;
    private EstadoReserva estado;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaLimitesPago;
    private BigDecimal montoTotal;
    private BigDecimal precioBase;


    private Long idPropiedad;
    private String ubicacion;
    private EstadoPropiedad estadoPropiedad;
    private EstadoReserva estadoReserva;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaLimitePago;
    private String temporada;
    private Double multiplicador;


    private Long idInquilino;
    private String nombre;
    private String email;

    private Long idMensaje;
    private LocalDateTime fecha;
    private String contenido;
    private Long idEmisor;
    private Long idReceptor;

}
