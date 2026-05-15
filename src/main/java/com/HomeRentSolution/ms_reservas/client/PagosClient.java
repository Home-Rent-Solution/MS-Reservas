package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.ReservaPagosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "ms-pagos", url = "${ms-pagos.url}")
public interface PagosClient {

    @GetMapping("/api/pagos/{id}")
    Boolean getPagoPorId(@PathVariable ("id") Long idPago);

    @PostMapping("/api/pagos/crear")
    void crearPago(@RequestBody ReservaPagosDTO pagoRequest);

    @PutMapping("/api/pagos/cancelar/{id}")
    void cancelarPago(@PathVariable ("id")Long idReserva, @RequestParam String motivo, BigDecimal montoReembolso);

    void confirmarPago(Long idReserva);
}
