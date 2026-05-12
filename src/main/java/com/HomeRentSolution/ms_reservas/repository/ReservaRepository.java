package com.HomeRentSolution.ms_reservas.repository;

import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {


        List<Reserva> findByIdInquilino(Long idInquilino);
        List<Reserva> findByEstado(EstadoReserva estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

        @Query("SELECT COUNT(r) > 0 FROM Reserva r " +
                "WHERE r.idPropiedad = :idPropiedad " +
                "AND r.estado IN (com.tu.paquete.EstadoReserva.CONFIRMADA, com.tu.paquete.EstadoReserva.PENDIENTE) " +
                "AND (:inicio < r.fechaFin AND :fin > r.fechaInicio)")
        boolean existsConflict(@Param("idPropiedad") Long idPropiedad,
                               @Param("inicio") LocalDateTime inicio,
                               @Param("fin") LocalDateTime fin);


}

