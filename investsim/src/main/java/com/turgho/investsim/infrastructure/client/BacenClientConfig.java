package com.turgho.investsim.infrastructure.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// Timeouts configuraveis via application.properties (nunca hardcoded)
@Configuration
public class BacenClientConfig {

    @Bean
    RestClient bacenRestClient(
            @Value("${investsim.sgs.connect-timeout:5s}") Duration connectTimeout,
            @Value("${investsim.sgs.read-timeout:5s}") Duration readTimeout) {

        // SimpleClientHttpRequestFactory: suporta connect + read timeout
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
            .baseUrl("https://api.bcb.gov.br/dados/serie")
            .requestFactory(factory)
            .build();
    }
}
