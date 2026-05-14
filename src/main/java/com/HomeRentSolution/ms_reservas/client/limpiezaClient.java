package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaLimpiezaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-limpieza")
public interface limpiezaClient {

    @PostMapping("/api/limpieza/agendar")
    ReservaLimpiezaDTO agendarLimpieza(@RequestBody ReservaLimpiezaDTO dto);

    @PutMapping("/api/limpieza/{id}/cancelar")
    void cancelarLimpieza(@PathVariable("id") Long idLimpieza);
}
