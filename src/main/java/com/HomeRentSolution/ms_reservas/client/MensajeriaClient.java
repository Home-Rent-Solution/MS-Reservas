package com.HomeRentSolution.ms_reservas.client;

import com.HomeRentSolution.ms_reservas.dto.ReservaMensajeriaDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MensajeriaClient {

    @PostMapping("/api/mensajeria/email")
    ReservaMensajeriaDTO enviarEmail(@RequestBody ReservaMensajeriaDTO dto);
}
