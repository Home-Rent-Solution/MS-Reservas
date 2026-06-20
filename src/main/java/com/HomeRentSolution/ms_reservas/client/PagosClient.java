package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.PagosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "ms-pagos", url = "${ms.pagos.url}")
public interface PagosClient {

    @GetMapping("/api/pagos/{id}")
    Boolean getPagoPorId(@PathVariable("id") Long idPago);

    @PostMapping("/api/v1/pagos")
    void crearPago(@RequestBody PagosDTO pagoRequest);

    @PutMapping("/api/v1/pagos/{id}/cancelar")
    void cancelarPago(
            @PathVariable("id") Long idReserva,
            @RequestParam String motivo,
            @RequestParam BigDecimal montoReembolso);

    @PutMapping("/api/v1/pagos/{id}/confirmar")
    void confirmarPago(@PathVariable("id") Long idReserva);

}
