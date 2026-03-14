package com.bookingapp.infrastructure.config;

import com.stripe.StripeClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StripeProperties.class)
public class StripeConfiguration {

    @Bean
    public StripeClient stripeClient(StripeProperties stripeProperties) {
        return new StripeClient(stripeProperties.getSecretKey());
    }
}
