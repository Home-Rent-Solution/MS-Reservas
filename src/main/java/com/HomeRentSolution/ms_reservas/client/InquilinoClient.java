package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ms.InquilinoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inquilinos", url = "${ms.inquilinos.url}")
public interface InquilinoClient {

    @GetMapping("/api/v1/inquilinos/{id}")
    InquilinoDTO obtenerInquilinoPorId(@PathVariable ("id") Long idInquilino);


}
