package com.HomeRentSolution.ms_reservas.repository;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {



        @Override
        Optional<Reserva> findById(Long idReserva);

        List<Reserva> findByIdInquilino(Long idInquilino);
        List<Reserva> findByEstadoReserva(EstadoReserva estadoReserva);
        List<Reserva> findByEstadoReservaAndFechaLimitesPagoBefore(
                EstadoReserva estado, LocalDateTime fecha);
        @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.idPropiedad = :idPropiedad " +
                "AND r.estadoReserva != com.HomeRentSolution.ms_reservas.model.EstadoReserva.CANCELADA " +
                "AND r.fechaInicio < :fin AND r.fechaFin > :inicio")
        boolean existeReservaEnRango(
                @Param("idPropiedad") Long idPropiedad,
                @Param("inicio") LocalDateTime inicio,
                @Param("fin") LocalDateTime fin
        );

}

