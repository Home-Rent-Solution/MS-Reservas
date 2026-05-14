package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaPagosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "ms-pagos", url = "${ms-pagos.url}")
public interface PagosClient {

    @GetMapping("/api/pagos/{idReserva}")
    Boolean verificarPagoReserva(@PathVariable Long idPago);

    @PostMapping("/api/pagos/crear")
    void crearPago(@RequestBody ReservaPagosDTO pagoRequest);

    @PutMapping("/api/pagos/cancelar/{idReserva}")
    void cancelarPago(@PathVariable Long idReserva, @RequestParam String motivo, BigDecimal montoReembolso);

    void confirmarPago(Long idReserva);
}
