package com.bookingapp.infrastructure.kafka;

import com.bookingapp.domain.event.AccommodationCreatedEvent;
import com.bookingapp.domain.event.BookingCanceledEvent;
import com.bookingapp.domain.event.BookingCreatedEvent;
import com.bookingapp.domain.event.BookingExpiredEvent;
import com.bookingapp.domain.event.PaymentSucceededEvent;
import com.bookingapp.infrastructure.telegram.TelegramMessageFormatter;
import com.bookingapp.infrastructure.telegram.TelegramNotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class TelegramEventConsumer {
    private final ObjectMapper objectMapper;
    private final TelegramMessageFormatter telegramMessageFormatter;
    private final TelegramNotificationService telegramNotificationService;

    public TelegramEventConsumer(
            ObjectMapper objectMapper,
            TelegramMessageFormatter telegramMessageFormatter,
            TelegramNotificationService telegramNotificationService
    ) {
        this.objectMapper = objectMapper;
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.telegramNotificationService = telegramNotificationService;
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.bookingCreated}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeBookingCreated(String payload) {
        BookingCreatedEvent event = readValue(payload, BookingCreatedEvent.class);
        telegramNotificationService
                .sendMessage(telegramMessageFormatter.formatBookingCreatedEvent(event));
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.bookingCanceled}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeBookingCanceled(String payload) {
        BookingCanceledEvent event = readValue(payload, BookingCanceledEvent.class);
        telegramNotificationService
                .sendMessage(telegramMessageFormatter.formatBookingCanceledEvent(event));
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.accommodationCreated}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeAccommodationCreated(String payload) {
        AccommodationCreatedEvent event = readValue(payload, AccommodationCreatedEvent.class);
        telegramNotificationService.sendMessage(
                telegramMessageFormatter.formatAccommodationCreatedEvent(event)
        );
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.paymentSucceeded}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumePaymentSucceeded(String payload) {
        PaymentSucceededEvent event = readValue(payload, PaymentSucceededEvent.class);
        telegramNotificationService.sendMessage(
                telegramMessageFormatter.formatPaymentSucceededEvent(event)
        );
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.bookingExpired}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeBookingExpired(String payload) {
        BookingExpiredEvent event = readValue(payload, BookingExpiredEvent.class);
        telegramNotificationService.sendMessage(
                telegramMessageFormatter.formatBookingExpiredEvent(event)
        );
    }

    private <T> T readValue(String payload, Class<T> targetType) {
        try {
            return objectMapper.readValue(payload, targetType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Failed to deserialize Kafka payload to " + targetType.getSimpleName(),
                    exception
            );
        }
    }
}
