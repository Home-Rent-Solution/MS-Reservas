package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaPagosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-pagos", url = "${ms-pagos.url}")
public interface PagosClient {

    @GetMapping("/api/pagos/{idReserva}")
    Boolean verificarPagoReserva(@PathVariable Long idPago);

    @PostMapping("/api/pagos/crear")
    void crearPago(@RequestBody ReservaPagosDTO pagoRequest);
}
