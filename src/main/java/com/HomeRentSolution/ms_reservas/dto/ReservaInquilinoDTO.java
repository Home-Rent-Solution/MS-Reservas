package com.HomeRentSolution.ms_reservas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReservaInquilinoDTO {

    private Long idInquilino;

    private String nombre;

    private String email;

    private List<String> historialReservas = new ArrayList<>();

    private boolean bloqueado = false;
}
