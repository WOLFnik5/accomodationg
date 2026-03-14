package com.bookingapp.adapter.in.web.support;

import com.bookingapp.application.port.out.integration.EventPublisherPort;
import com.bookingapp.application.port.out.integration.PaymentProviderPort;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ControllerIntegrationTestConfiguration {

    @Bean
    @Primary
    EventPublisherPort eventPublisherPort() {
        return Mockito.mock(EventPublisherPort.class);
    }

    @Bean
    @Primary
    PaymentProviderPort paymentProviderPort() {
        return Mockito.mock(PaymentProviderPort.class);
    }
}
