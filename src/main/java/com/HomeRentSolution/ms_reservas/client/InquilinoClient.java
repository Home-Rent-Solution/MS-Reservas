package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.InquilinoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inquilino", url = "${ms-inquilinos.url}")
public interface InquilinoClient {

    @GetMapping("/inquilino/{id}")
    InquilinoDTO obtenerInquilinoPorId(@PathVariable ("id") Long idInquilino);


}
