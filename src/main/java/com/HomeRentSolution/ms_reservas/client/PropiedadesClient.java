package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaPropiedadDTO;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.model.Reserva;
import feign.Param;
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
public interface PropiedadesClient {

    @GetMapping("/api/v1/propiedades/{id}")
    ReservaPropiedadDTO obtenerPropiedadPorId(@PathVariable("id") Long idPropiedad);

    @GetMapping("/api/v1/propiedades")
    List<ReservaPropiedadDTO> obtenerTodas();

    @GetMapping("/api/v1/propiedades/buscar")
    List<ReservaPropiedadDTO> buscarPorUbicacionYPrecio(
            @RequestParam String ubicacion,
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax
    );

    @GetMapping("/api/v1/propiedades/disponibles")
    List<ReservaPropiedadDTO> getPropiedadesDisponibles();

    @PutMapping("/api/v1/propiedades/{id}/estado")
    void cambiarEstado(@PathVariable("id") Long id);
}
