package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.ReservaPrecioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-precios", url = "${ms.precios.url}")
public interface PrecioClient {

    @GetMapping("/api/precio/{id}")
    Boolean getPrecioPorId(@PathVariable ("id")Long idPrecios);

    @GetMapping("/api/precio/propiedad/{idPropiedad}")
    ReservaPrecioDTO obtenerPrecioPorPropiedad(@PathVariable Long idPropiedad);
}
