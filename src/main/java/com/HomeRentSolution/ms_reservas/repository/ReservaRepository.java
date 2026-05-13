package com.HomeRentSolution.ms_reservas.repository;

import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import com.HomeRentSolution.ms_reservas.model.EstadoPropiedad;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {


        List<Reserva> findByBuscarPorIdInquilino(Long idInquilino);
        List<Reserva> findByEstado(EstadoReserva estado);
        List<Reserva> findByEstado(EstadoPropiedad estadoPropiedad);
        List<Reserva> findByEstadoAndFechaLimitePagoBefore(EstadoReserva estado, LocalDateTime fecha);

        @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.idPropiedad = :idPropiedad " +
                "AND r.estado != 'CANCELADA' " +
                "AND r.fechaInicio < :fin AND r.fechaFin > :inicio")
        boolean existeReservaEnRango(
                @Param("idPropiedad") Integer idPropiedad,
                @Param("inicio") LocalDate inicio,
                @Param("fin") LocalDate fin
        );


}

