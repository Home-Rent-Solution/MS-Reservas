package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.dto.request.ReservaCancelarRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltroRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaDetalleResponse;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API de gestión de reservas de propiedades")
public class ReservaController {

    private final ReservaService reservasService;

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodas() {
        return ResponseEntity.ok(reservasService.buscarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reserva>> buscarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reservasService.buscarPorEstado(EstadoReserva.valueOf(estado)));
    }

    @GetMapping("/{id}/cliente")
    public ResponseEntity<ReservaResponse> getParaCliente(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.obtenerParaCliente(id));
    }

    @GetMapping("/{id}/admin")
    public ResponseEntity<ReservaDetalleResponse> getParaAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.obtenerParaAdmin(id));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(@RequestBody ReservaCrearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservasService.crearReserva(request));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ReservaDetalleResponse>> buscarDisponibles(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        ReservaFiltroRequest filtro = new ReservaFiltroRequest();
        filtro.setFechaInicio(fechaInicio.atStartOfDay());
        filtro.setFechaFin(fechaFin.atStartOfDay());
        filtro.setUbicacion(ubicacion);
        filtro.setPrecioMin(precioMin);
        filtro.setPrecioMax(precioMax);
        return ResponseEntity.ok(reservasService.buscarConFiltro(filtro));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponse> confirmarReserva(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.confirmarReserva(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @PathVariable Long id,
            @RequestBody ReservaCancelarRequest request) {
        return ResponseEntity.ok(reservasService.cancelarReserva(id, request.getMotivo()));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ReservaDetalleResponse> finalizarReserva(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.finalizarReserva(id));
    }

}
