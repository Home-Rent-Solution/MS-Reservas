package com.HomeRentSolution.ms_reservas.config;

import feign.Request;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Appconfig {

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(5000, 10000);
    }

    public static final String RESERVAS_EXCHANGE = "reservas.exchange";
    public static final String PAGOS_EXCHANGE = "pagos.exchange"; // NUEVO: exchange del publicador

    public static final String ROUTING_CREADA = "reserva.creada";
    public static final String ROUTING_CANCELADA = "reserva.cancelada";
    public static final String ROUTING_FINALIZADA = "reserva.finalizada";

    public static final String QUEUE_PAGO_CREADO = "reservas.pago-creado.queue";
    public static final String QUEUE_PAGO_ELIMINADO = "reservas.pago-eliminado.queue";

    @Bean
    public TopicExchange reservasExchange() {
        return new TopicExchange(RESERVAS_EXCHANGE);
    }

    @Bean
    public TopicExchange pagosExchange() {
        return new TopicExchange(PAGOS_EXCHANGE);
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
        return BindingBuilder.bind(pagoCreadoQueue).to(pagosExchange).with("pago.creado");
    }

    @Bean
    public Binding bindingPagoEliminado(Queue pagoEliminadoQueue, TopicExchange pagosExchange) {
        return BindingBuilder.bind(pagoEliminadoQueue).to(pagosExchange).with("pago.eliminado");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
