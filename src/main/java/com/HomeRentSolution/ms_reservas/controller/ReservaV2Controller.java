package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.assemblers.ReservaAssemblers;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCancelarRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Reservas V2", description = "Endpoints de reservas con navegación por enlaces")
public class ReservaV2Controller {

    private final ReservaService reservaService;
    private final ReservaAssemblers assembler;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva HATEOAS")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return ResponseEntity.ok(assembler.toModel(reserva));
    }

    @GetMapping
    @Operation(summary = "Listar reservas HATEOAS")
    public ResponseEntity<CollectionModel<ReservaResponse>> obtenerTodas() {
        List<ReservaResponse> reservas = reservaService.buscarTodas().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(reservas));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva HATEOAS")
    public ResponseEntity<ReservaResponse> confirmar(@PathVariable Long id) {
        reservaService.confirmarReserva(id);
        return ResponseEntity.ok(assembler.toModel(reservaService.buscarPorId(id)));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva HATEOAS")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id, @RequestBody ReservaCancelarRequest request) {
        reservaService.cancelarReserva(id, request != null ? request.getMotivo() : null);
        return ResponseEntity.ok(assembler.toModel(reservaService.buscarPorId(id)));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar reserva HATEOAS")
    public ResponseEntity<ReservaResponse> finalizar(@PathVariable Long id) {
        reservaService.finalizarReserva(id);
        return ResponseEntity.ok(assembler.toModel(reservaService.buscarPorId(id)));
    }
}
