package com.HomeRentSolution.ms_reservas.config;

import feign.Request;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(
                Duration.ofMillis(5000),
                Duration.ofMillis(10000),
                true
        );
    }

    // --- Exchange propio de Reserva (queda para eventos que no son pagos/limpieza,
    //     p.ej. reserva.finalizada, útil para mensajería/comentarios a futuro) ---
    public static final String RESERVAS_EXCHANGE = "reservas.exchange";
    public static final String ROUTING_FINALIZADA = "reserva.finalizada";

    // --- Exchange de PAGOS (no es propiedad de Reserva, solo lo referenciamos) ---
    public static final String PAGOS_EXCHANGE = "pagos.exchange";
    public static final String ROUTING_RESERVA_CREADA_PAGOS = "reserva.creada";
    public static final String ROUTING_RESERVA_CANCELADA_PAGOS = "reserva.cancelada";
    public static final String QUEUE_PAGO_CREADO = "reservas.pago-creado.queue";
    public static final String QUEUE_PAGO_ELIMINADO = "reservas.pago-eliminado.queue";
    public static final String ROUTING_PAGO_CREADO = "pago.creado";
    public static final String ROUTING_PAGO_ELIMINADO = "pago.eliminado";

    // --- Exchange de LIMPIEZA (no es propiedad de Reserva, solo lo referenciamos) ---
    public static final String LIMPIEZAS_EXCHANGE = "limpiezas.exchange";
    public static final String ROUTING_RESERVA_CREADA_LIMPIEZA = "reserva.creada";
    public static final String ROUTING_RESERVA_CANCELADA_LIMPIEZA = "reserva.cancelada";

    @Bean
    public TopicExchange reservasExchange() {
        return new TopicExchange(RESERVAS_EXCHANGE);
    }

    @Bean
    public TopicExchange pagosExchange() {
        return new TopicExchange(PAGOS_EXCHANGE);
    }

    @Bean
    public TopicExchange limpiezasExchange() {
        return new TopicExchange(LIMPIEZAS_EXCHANGE);
    }

    @Bean
    public Queue pagoCreadoQueue() {
        return new Queue(QUEUE_PAGO_CREADO, true);
    }

    @Bean
    public Queue pagoEliminadoQueue() {
        return new Queue(QUEUE_PAGO_ELIMINADO, true);
    }

    @Bean
    public Binding bindingPagoCreado(Queue pagoCreadoQueue, TopicExchange pagosExchange) {
        return BindingBuilder.bind(pagoCreadoQueue).to(pagosExchange).with(ROUTING_PAGO_CREADO);
    }

    @Bean
    public Binding bindingPagoEliminado(Queue pagoEliminadoQueue, TopicExchange pagosExchange) {
        return BindingBuilder.bind(pagoEliminadoQueue).to(pagosExchange).with(ROUTING_PAGO_ELIMINADO);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}