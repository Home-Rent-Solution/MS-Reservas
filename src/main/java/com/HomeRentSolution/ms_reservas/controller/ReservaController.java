package com.HomeRentSolution.ms_reservas.controller;

import com.HomeRentSolution.ms_reservas.dto.request.ReservaCancelarRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaCrearRequest;
import com.HomeRentSolution.ms_reservas.dto.request.ReservaFiltroRequest;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaDetalleResponse;
import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import com.HomeRentSolution.ms_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API de gestión de reservas de propiedades")
public class ReservaController {

    private final ReservaService reservasService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada", content = @Content(schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<Reserva> obtenerPorId(@Parameter(description = "ID de la reserva", example = "1", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(reservasService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas")
    @ApiResponse(responseCode = "200", description = "Listado completo de reservas")
    public ResponseEntity<List<Reserva>> obtenerTodas() {
        return ResponseEntity.ok(reservasService.buscarTodas());
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar reservas por estado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reservas filtradas"), @ApiResponse(responseCode = "400", description = "Estado inválido")})
    public ResponseEntity<List<Reserva>> buscarPorEstado(
            @Parameter(description = "Estado de reserva", example = "PENDIENTE", required = true) @PathVariable String estado) {
        return ResponseEntity.ok(reservasService.buscarPorEstado(EstadoReserva.valueOf(estado)));
    }

    @GetMapping("/{id}/cliente")
    @Operation(summary = "Obtener vista de cliente", description = "Retorna los datos esenciales de la reserva para el inquilino.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Vista de cliente", content = @Content(schema = @Schema(implementation = ReservaResponse.class))), @ApiResponse(responseCode = "404", description = "Reserva no encontrada")})
    public ResponseEntity<ReservaResponse> getParaCliente(@Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservasService.obtenerParaCliente(id));
    }

    @GetMapping("/{id}/admin")
    @Operation(summary = "Obtener vista administrativa", description = "Enriquece la reserva con información remota de propiedad, precio e inquilino.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Detalle administrativo", content = @Content(schema = @Schema(implementation = ReservaDetalleResponse.class))), @ApiResponse(responseCode = "404", description = "Reserva no encontrada")})
    public ResponseEntity<ReservaDetalleResponse> getParaAdmin(@Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservasService.obtenerParaAdmin(id));
    }

    @PostMapping
    @Operation(summary = "Crear reserva", description = "Valida la disponibilidad de la propiedad, crea una reserva pendiente y publica el evento de creación.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada", content = @Content(schema = @Schema(implementation = ReservaResponse.class), examples = @ExampleObject(value = "{\"idReserva\":1,\"idPropiedad\":10,\"idInquilino\":7,\"estado\":\"PENDIENTE\",\"fechaInicio\":\"2026-07-01T00:00:00\",\"fechaFin\":\"2026-07-05T00:00:00\"}"))),
            @ApiResponse(responseCode = "404", description = "Propiedad no encontrada"),
            @ApiResponse(responseCode = "409", description = "Propiedad no disponible")
    })
    public ResponseEntity<ReservaResponse> crearReserva(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Datos de la nueva reserva",
                    content = @Content(schema = @Schema(implementation = ReservaCrearRequest.class), examples = @ExampleObject(value = "{\"idPropiedad\":10,\"idInquilino\":7,\"fechaInicio\":\"2026-07-01\",\"fechaFin\":\"2026-07-05\"}")))
            @RequestBody ReservaCrearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservasService.crearReserva(request));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar propiedades disponibles", description = "Filtra propiedades disponibles por fechas, ubicación y rango de precio.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Propiedades disponibles"), @ApiResponse(responseCode = "400", description = "Fechas o rango inválidos")})
    public ResponseEntity<List<ReservaDetalleResponse>> buscarDisponibles(
            @Parameter(description = "Fecha inicial", example = "2026-07-01", required = true) @RequestParam LocalDate fechaInicio,
            @Parameter(description = "Fecha final", example = "2026-07-05", required = true) @RequestParam LocalDate fechaFin,
            @Parameter(description = "Ubicación", example = "Santiago") @RequestParam(required = false) String ubicacion,
            @Parameter(description = "Precio mínimo", example = "30000") @RequestParam(required = false) BigDecimal precioMin,
            @Parameter(description = "Precio máximo", example = "120000") @RequestParam(required = false) BigDecimal precioMax) {
        ReservaFiltroRequest filtro = new ReservaFiltroRequest();
        filtro.setFechaInicio(fechaInicio.atStartOfDay());
        filtro.setFechaFin(fechaFin.atStartOfDay());
        filtro.setUbicacion(ubicacion);
        filtro.setPrecioMin(precioMin);
        filtro.setPrecioMax(precioMax);
        return ResponseEntity.ok(reservasService.buscarConFiltro(filtro));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva", description = "Confirma el pago, actualiza la propiedad y envía la notificación al inquilino.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reserva confirmada"), @ApiResponse(responseCode = "404", description = "Reserva no encontrada"), @ApiResponse(responseCode = "409", description = "Estado o plazo incompatible")})
    public ResponseEntity<ReservaResponse> confirmarReserva(@Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservasService.confirmarReserva(id));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva", description = "Cancela la reserva, calcula el reembolso y publica el evento de cancelación.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reserva cancelada"), @ApiResponse(responseCode = "404", description = "Reserva no encontrada"), @ApiResponse(responseCode = "409", description = "La reserva no admite cancelación")})
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Motivo de cancelación",
                    content = @Content(schema = @Schema(implementation = ReservaCancelarRequest.class), examples = @ExampleObject(value = "{\"motivo\":\"Cambio de fechas del viaje\"}")))
            @RequestBody ReservaCancelarRequest request) {
        return ResponseEntity.ok(reservasService.cancelarReserva(id, request.getMotivo()));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar reserva", description = "Marca una reserva confirmada como finalizada y publica el evento correspondiente.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reserva finalizada"), @ApiResponse(responseCode = "404", description = "Reserva no encontrada"), @ApiResponse(responseCode = "409", description = "Estado incompatible")})
    public ResponseEntity<ReservaDetalleResponse> finalizarReserva(@Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservasService.finalizarReserva(id));
    }

}
