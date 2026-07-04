package com.HomeRentSolution.ms_reservas.listeners;

import com.HomeRentSolution.ms_reservas.dto.ms.PagosDTO;
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
    public void recibirPagoCreado(PagosDTO evento) {
        log.info("[RabbitMQ] MS-Reservas recibió registro de pago para la Reserva ID: {}", evento.getIdReserva());
        reservaRepository.findById(evento.getIdReserva()).ifPresentOrElse(
                reserva -> log.info("Pago vinculado correctamente a la Reserva ID: {}", reserva.getIdReserva()),
                () -> log.warn("No se encontró la Reserva ID: {} al recibir el evento de pago creado", evento.getIdReserva())
        );
    }

    @RabbitListener(queues = "reservas.pago-eliminado.queue")
    public void recibirPagoEliminado(PagosDTO evento) {
        log.info("[RabbitMQ] MS-Reservas recibió cancelación de pago para la Reserva ID: {}", evento.getIdReserva());
        reservaRepository.findById(evento.getIdReserva()).ifPresent(reserva -> {
            if (reserva.getEstadoReserva() != EstadoReserva.CANCELADA
                    && reserva.getEstadoReserva() != EstadoReserva.FINALIZADA) {
                reserva.setEstadoReserva(EstadoReserva.CANCELADA);
                reservaRepository.save(reserva);
                log.info("[RabbitMQ] Reserva ID: {} cancelada automáticamente por eliminación de pago", reserva.getIdReserva());
            }
        });
    }

}
