package com.HomeRentSolution.ms_reservas.dto.ms;

import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ReservaPropiedadDTO {

    private Long idPropiedad;

    private String titulo;

    private String descripcion;

    private String ubicacion;

    private BigDecimal precio;

    private Long idAnfitrion;

    private String tipo;

    private EstadoPropiedad estadoPropiedad;

    private boolean disponible = false;
}
