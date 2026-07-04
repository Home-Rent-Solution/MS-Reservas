package com.HomeRentSolution.ms_reservas.listeners;

import com.HomeRentSolution.ms_reservas.dto.response.ReservaResponse;
import com.HomeRentSolution.ms_reservas.model.EstadoReserva;
import com.HomeRentSolution.ms_reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservaListeners {

    private final ReservaRepository reservaRepository;

    @RabbitListener(queues = "reservas.pago-creado.queue")
    public void recibirPagoCreado(ReservaResponse evento) {
        log.info("[RabbitMQ] MS-Reserva recibió confirmación de pago creado para Reserva ID: {}", evento.getIdReserva());
    }

    @RabbitListener(queues = "reservas.pago-eliminado.queue")
    public void recibirPagoEliminado(ReservaResponse evento) {
        log.info("[RabbitMQ] MS-Reserva recibió notificación de pago eliminado para Reserva ID: {}", evento.getIdReserva());
        reservaRepository.findById(evento.getIdReserva()).ifPresent(r -> {
            r.setEstadoReserva(EstadoReserva.CANCELADA);
            reservaRepository.save(r);
        });
    }
}
