package com.HomeRentSolution.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-especialidades",
url = "${ms.especialidades.url}")

public interface EspecialidadClient {

    @GetMapping("/api/especialidades/{id}")
    String obtenetPorId(@PathVariable Long id);
}
