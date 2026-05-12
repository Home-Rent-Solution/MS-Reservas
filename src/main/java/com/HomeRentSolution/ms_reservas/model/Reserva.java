package com.HomeRentSolution.ms_reservas.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import jakarta.persistence.Table;
=======
>>>>>>> 431d61f80c76c06474bc904525e872f4cfd1c989
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
=======
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
>>>>>>> 431d61f80c76c06474bc904525e872f4cfd1c989
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idReserva;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "id_inquilino", nullable = false)
    private Long idInquuilino;

    @Column(name = "id_propiedad", nullable = false)
    private Long idPropiedad;
<<<<<<< HEAD
=======



>>>>>>> 431d61f80c76c06474bc904525e872f4cfd1c989

}
