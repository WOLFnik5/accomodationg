package com.bookingapp.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramConfiguration {

    @Bean
    public RestClient telegramRestClient(TelegramProperties telegramProperties) {
        return RestClient.builder()
                .baseUrl(telegramProperties.getBaseUrl())
                .build();
    }
}
