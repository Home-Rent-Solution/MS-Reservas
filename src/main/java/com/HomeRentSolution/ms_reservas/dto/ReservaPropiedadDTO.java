package com.HomeRentSolution.ms_reservas.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ReservaPropiedadDTO {

    private Integer idPropiedad;

    private String titulo;

    private String descripcion;

    private String ubicacion;

    private BigDecimal precio;

    private Integer idAnfitrion;

    private String tipo;

    private boolean disponible = false;
}
