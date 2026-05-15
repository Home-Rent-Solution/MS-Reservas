package com.HomeRentSolution.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inquilino", url = "${ms-inquilinos.url}")
public interface InquilinoClient {

    @GetMapping("/inquilino/{id}")
    Object obtenerInquilinoPorId(@PathVariable ("id") Long idInquilino);


}
