package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.ReservaMensajeriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-mensajeria", url = "${ms-mensajeria.url}")
public interface MensajeriaClient {

    @GetMapping("/mensajeria/{id}")
    Object getMensajeriaPorId(@PathVariable("id") Long idMensaje);

    @PostMapping("/api/mensajeria/email")
    ReservaMensajeriaDTO enviarEmail(@RequestBody ReservaMensajeriaDTO dto);
}
