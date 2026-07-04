package com.HomeRentSolution.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-limpieza", url = "${ms.limpieza.url}")
public interface LimpiezaClient {

    @GetMapping("/api/v1/limpiezas/{id}")
    LimpiezaDTO getLimpiezaPorId(@PathVariable("id") Long idLimpieza);

    @GetMapping("/api/v1/limpiezas")
    List<LimpiezaDTO> obtenerTodas();


}
