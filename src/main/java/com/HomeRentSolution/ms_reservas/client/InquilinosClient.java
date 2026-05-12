package com.HomeRentSolution.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inquilinos", url = "${ms-inquilinos.url}")
public interface InquilinosClient {

    @GetMapping("/inquilinos/{id}")
    Object getInquilinoPorId(@PathVariable Long id);
}
