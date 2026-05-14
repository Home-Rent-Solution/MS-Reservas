package com.HomeRentSolution.ms_reservas.dto;

import lombok.Data;

@Data
public class ReservaPrecioDTO {

    private Long idPrecios;

    private String temporada;

    private Double multiplicador;

    private Long idPropiedad;
}
