package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.dto.request.ReservaCancelarRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltroRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaDetalleResponse;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.dto.ms.InquilinoDTO;
import com.HomeRentSolution.ms_reservas.exception.PropiedadNoDisponibleException;
import com.HomeRentSolution.ms_reservas.exception.RecursoNoEncontradoException;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.HomeRentSolution.ms_reservas.service.ReservaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservasService;

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {

        Reserva reserva = reservasService.buscarPorId(id);

        return ResponseEntity.ok(reserva);
    }

    @GetMapping
    public List<Reserva> obtenerTodas() {
        return reservasService.buscarTodas();
    }

    @GetMapping("/estado/{estado}")
    public List<Reserva> buscarPorEstado(@PathVariable String estado){

        return reservasService.buscarPorEstado(EstadoReserva.valueOf(estado));

    }



    // ─── GET /{id}/cliente → vista del inquilino (simple) ────────────────────
    @GetMapping("/{id}/cliente")
    public ResponseEntity<?> getParaCliente(@PathVariable Long id) {
        try {
            ReservaResponse dto = reservasService.obtenerParaCliente(id);
            return ResponseEntity.ok(dto);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── GET /{id}/admin → vista del administrador (detallada) ───────────────
    @GetMapping("/{id}/admin")
    public ResponseEntity<?> getParaAdmin(@PathVariable Long id) {
        try {
            ReservaDetalleResponse dto = reservasService.obtenerParaAdmin(id);
            return ResponseEntity.ok(dto);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── POST / → crear reserva ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody ReservaCrearRequest request) {
        try {
            ReservaResponse response = reservasService.crearReserva(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (PropiedadNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── GET /buscar → propiedades disponibles con filtros ───────────────────
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDisponibles(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax
    ) {
        try {
            ReservaFiltroRequest filtro = new ReservaFiltroRequest();
            filtro.setFechaInicio(fechaInicio.atStartOfDay());
            filtro.setFechaFin(fechaFin.atStartOfDay());
            filtro.setUbicacion(ubicacion);
            filtro.setPrecioMin(precioMin);
            filtro.setPrecioMax(precioMax);

            return ResponseEntity.ok(reservasService.buscarConFiltro(filtro));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── PUT /{id}/confirmar → confirmar reserva ──────────────────────────────
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservasService.confirmarReserva(id));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── PUT /{id}/cancelar → cancelar reserva ────────────────────────────────
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Long id,
            @RequestBody ReservaCancelarRequest request
    ) {
        try {
            return ResponseEntity.ok(reservasService.cancelarReserva(id, request.getMotivo()));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ─── PUT /{id}/finalizar → finalizar reserva ──────────────────────────────
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarReserva(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservasService.finalizarReserva(id));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
