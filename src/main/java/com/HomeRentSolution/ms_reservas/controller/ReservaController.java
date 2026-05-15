package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.dto.ms.ReservaInquilinoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.HomeRentSolution.ms_reservas.service.ReservaService;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservasService;

    public ReservaController(ReservaService reservasService) {
        this.reservasService = reservasService;
    }

    // Vista cliente
    @GetMapping("/{id}/cliente")
    public ResponseEntity<?> getParaCliente(@PathVariable Long id) {
        try {
            ReservaInquilinoDTO dto = reservasService.obtenerParaCliente(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Vista administrador
    @GetMapping("/{id}/admin")
    public ResponseEntity<?> getParaAdmin(@PathVariable Long id) {
        try {
            ReservaResponse dto = reservasService.obtenerParaAdmin(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Crear reserva
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody ReservaInquilinoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(reservasService.crearReserva(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Buscar propiedades disponibles por rango de fechas
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDisponibles(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) String ubicacion
    ) {
        try {
            return ResponseEntity.ok(
                    reservasService.buscarDisponibles(fechaInicio, fechaFin, ubicacion)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
