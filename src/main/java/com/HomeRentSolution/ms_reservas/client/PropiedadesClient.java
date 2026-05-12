package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "ms-propiedades",
url = "${ms.propiedades.url}")
@EnableScheduling
public interface PropiedadesClient {

    @GetMapping("/api/propiedades/{id}")
    ReservaPropiedadDTO obtenerPropiedadPorId(@PathVariable("id") Long id);

    @GetMapping("/api/propiedades")
    List<ReservaPropiedadDTO> obtenerTodas();

    @GetMapping("/propiedades/buscar")
    List<ReservaPropiedadDTO> buscarPorUbicacionYPrecio(
            @RequestParam String ubicacion,
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax
    );

    @GetMapping("/propiedades/disponibles")
    List<ReservaPropiedadDTO> getPropiedadesDisponibles();

    @PutMapping("/api/propiedades/{id}/estado")
    void cambiarEstado(@PathVariable("id") Long id);
}
