package com.HomeRentSolution.ms_reservas.assemblers;

import com.HomeRentSolution.ms_reservas.controller.ReservaV2Controller;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ReservaAssemblers extends RepresentationModelAssemblerSupport<Reserva, ReservaResponse> {

    public ReservaAssemblers() {
        super(ReservaV2Controller.class, ReservaResponse.class);
    }

    @Override
    public ReservaResponse toModel(Reserva entidad) {
        ReservaResponse dto = new ReservaResponse();
        dto.setIdReserva(entidad.getIdReserva());
        dto.setIdPropiedad(entidad.getIdPropiedad());
        dto.setIdInquilino(entidad.getIdInquilino());
        dto.setEstado(entidad.getEstadoReserva());
        dto.setFechaReserva(entidad.getFechaReserva());
        dto.setFechaInicio(entidad.getFechaInicio());
        dto.setFechaFin(entidad.getFechaFin());
        dto.setFechaLimitesPago(entidad.getFechaLimitesPago());
        dto.setMontoTotal(entidad.getMontoTotal());

        dto.add(linkTo(methodOn(ReservaV2Controller.class).obtenerPorId(entidad.getIdReserva())).withSelfRel());

        if (entidad.getEstadoReserva() == EstadoReserva.PENDIENTE) {
            dto.add(linkTo(methodOn(ReservaV2Controller.class).confirmar(entidad.getIdReserva())).withRel("confirmar"));
            dto.add(linkTo(methodOn(ReservaV2Controller.class).cancelar(entidad.getIdReserva(), null)).withRel("cancelar"));
        } else if (entidad.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
            dto.add(linkTo(methodOn(ReservaV2Controller.class).finalizar(entidad.getIdReserva())).withRel("finalizar"));
            dto.add(linkTo(methodOn(ReservaV2Controller.class).cancelar(entidad.getIdReserva(), null)).withRel("cancelar"));
        }

        return dto;
    }
}
