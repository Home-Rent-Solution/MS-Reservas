package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.LimpiezaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-limpieza", url = "${ms.inquilinos.url}")
public interface LimpiezaClient {

    @GetMapping("/api/v1/limpiezas/{id}")
    LimpiezaDTO getLimpiezaPorId(@PathVariable("id") Long idLimpieza);

    @PostMapping("/api/v1/limpiezas")
    LimpiezaDTO agendarLimpieza(@RequestBody LimpiezaDTO dto);

    @PutMapping("/api/v1/limpiezas/{id}/estado")
    LimpiezaDTO cancelarPorSistema(
            @PathVariable("id") Long idLimpieza,
            @RequestParam String nuevoEstado);

}
