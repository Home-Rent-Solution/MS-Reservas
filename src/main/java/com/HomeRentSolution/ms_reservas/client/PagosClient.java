package com.HomeRentSolution.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pagos", url = "${ms-pagos.url}")
public interface PagosClient {

    @GetMapping("/pagos/reserva/{idReserva}")
    Boolean verificarPagoReserva(@PathVariable int idReserva);
}
