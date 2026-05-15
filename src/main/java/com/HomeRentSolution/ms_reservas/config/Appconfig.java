package com.HomeRentSolution.ms_reservas.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Appconfig {
    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(5000, 10000); // 5s conexión, 10s lectura
    }
}
