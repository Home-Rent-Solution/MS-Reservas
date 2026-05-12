package com.HomeRentSolution.ms_reservas.dto;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaAdmDTO {

    private Integer idReserva;
    private Integer idPropiedad;
    private Integer idInquilino;
    private String nombreInquilino;
    private String tituloPropiedad;
    private String ubicacion;
    private Propiedades.TipoPropiedad tipoPropiedad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal montoTotal;
    private String estado;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaLimitePago;
}
