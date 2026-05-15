package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.PropiedadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "ms-propiedades", url = "${ms.propiedades.url}")
public interface PropiedadesClient {

    @GetMapping("/api/v1/propiedades/{id}")
    PropiedadDTO obtenerPropiedadPorId(@PathVariable("id") Long idPropiedad);

    @GetMapping("/api/v1/propiedades")
    List<PropiedadDTO> obtenerTodas();

    @GetMapping("/api/v1/propiedades/buscar")
    List<PropiedadDTO> buscarPorUbicacionYPrecio(
            @RequestParam String ubicacion,
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax
    );

    @GetMapping("/api/v1/propiedades/disponibles")
    List<PropiedadDTO> getPropiedadesDisponibles();

    @PutMapping("/api/v1/propiedades/{id}/estado")
    void cambiarEstado(@PathVariable("id") Long id);
}
