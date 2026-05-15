package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.LimpiezaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-limpieza", url = "${ms-inquilinos.url}")
public interface LimpiezaClient {

    @GetMapping("/limpieza/{id}")
    Object getLimpiezaPorId(@PathVariable ("id") Long idLimpieza);

    @PostMapping("/api/limpieza/agendar")
    LimpiezaDTO agendarLimpieza(@RequestBody LimpiezaDTO dto);

    @PutMapping("/api/limpieza/{id}/cancelar")
    void cancelarLimpieza(@PathVariable("id") Long idLimpieza);
}
