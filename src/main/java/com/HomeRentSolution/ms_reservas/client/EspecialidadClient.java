package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-especialidades",
url = "${ms.especialidades.url}")

public interface EspecialidadClient {

    @GetMapping("/api/propiedades") // El endpoint que exponga tu compañero
    List<ReservaPropiedadDTO> obtenerTodas();

    @GetMapping("/api/especialidades/{id}")
    String obtenetPorId(@PathVariable Long id);
}
