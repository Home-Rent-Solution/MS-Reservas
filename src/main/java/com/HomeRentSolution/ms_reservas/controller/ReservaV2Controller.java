package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.assemblers.ReservaAssemblers;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCancelarRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas V2",
        description = "Endpoints de reservas con enlaces de navegación según el estado actual")
public class ReservaV2Controller {

    private final ReservaService reservasService;
    private final ReservaAssemblers assembler;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva HATEOAS",
            description = "Retorna la reserva con los enlaces de acción disponibles según su estado.")
    public ResponseEntity<ReservaResponse> obtenerPorId(
            @Parameter(description = "ID de la reserva", example = "1", required = true) @PathVariable Long id) {
        Reserva reserva = reservasService.buscarPorId(id);
        return ResponseEntity.ok(assembler.toModel(reserva));
    }

    @GetMapping
    @Operation(summary = "Listar reservas HATEOAS",
            description = "Retorna la colección completa de reservas con sus enlaces de navegación.")
    public ResponseEntity<CollectionModel<ReservaResponse>> obtenerTodas() {
        List<ReservaResponse> reservas = reservasService.buscarTodas()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(reservas));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva (HATEOAS)")
    public ResponseEntity<ReservaResponse> confirmar(
            @Parameter(description = "ID de la reserva", example = "1", required = true) @PathVariable Long id) {
        reservasService.confirmarReserva(id);
        return ResponseEntity.ok(assembler.toModel(reservasService.buscarPorId(id)));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva (HATEOAS)")
    public ResponseEntity<ReservaResponse> cancelar(
            @Parameter(description = "ID de la reserva", example = "1", required = true) @PathVariable Long id,
            @RequestBody(required = false) ReservaCancelarRequest request) {
        String motivo = (request != null) ? request.getMotivo() : null;
        reservasService.cancelarReserva(id, motivo);
        return ResponseEntity.ok(assembler.toModel(reservasService.buscarPorId(id)));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar reserva (HATEOAS)")
    public ResponseEntity<ReservaResponse> finalizar(
            @Parameter(description = "ID de la reserva", example = "1", required = true) @PathVariable Long id) {
        reservasService.finalizarReserva(id);
        return ResponseEntity.ok(assembler.toModel(reservasService.buscarPorId(id)));
    }
}
