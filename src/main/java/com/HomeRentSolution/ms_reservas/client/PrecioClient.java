package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.PrecioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-precios", url = "${ms.precios.url}")
public interface PrecioClient {

    @GetMapping("/api/v1/precios/{id}")
    PrecioDTO getPrecioPorId(@PathVariable("id") Long idPrecios);

    @GetMapping("/api/v1/precios/propiedad/{id}")
    List<PrecioDTO> obtenerPreciosPorPropiedad(@PathVariable("id") Long idPropiedad);

    default PrecioDTO obtenerPrecioPorPropiedad(Long idPropiedad) {
        List<PrecioDTO> precios = obtenerPreciosPorPropiedad(idPropiedad);
        if (precios == null || precios.isEmpty()) {
            return null;
        }
        return precios.get(0);
    }
}
