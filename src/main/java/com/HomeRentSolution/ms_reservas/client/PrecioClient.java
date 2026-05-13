package com.HomeRentSolution.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-precios", url = "${ms.precios.url}")
@EnableScheduling
public interface PrecioClient {

    @GetMapping("/api/precio/{idPrecios}")
    Boolean verificarPrecioPorId(@PathVariable Long idPrecios);

}
