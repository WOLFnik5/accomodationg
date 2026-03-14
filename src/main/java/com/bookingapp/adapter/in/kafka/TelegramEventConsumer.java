package com.bookingapp.adapter.in.kafka;

import com.bookingapp.adapter.out.kafka.event.AccommodationCreatedEvent;
import com.bookingapp.adapter.out.kafka.event.BookingCanceledEvent;
import com.bookingapp.adapter.out.kafka.event.BookingCreatedEvent;
import com.bookingapp.adapter.out.kafka.event.BookingExpiredEvent;
import com.bookingapp.adapter.out.kafka.event.PaymentSucceededEvent;
import com.bookingapp.adapter.out.telegram.TelegramBotClient;
import com.bookingapp.adapter.out.telegram.TelegramMessageFormatter;
import com.bookingapp.infrastructure.config.KafkaTopicsProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TelegramEventConsumer {

    private final ObjectMapper objectMapper;
    private final TelegramBotClient telegramBotClient;
    private final TelegramMessageFormatter telegramMessageFormatter;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public TelegramEventConsumer(
            ObjectMapper objectMapper,
            TelegramBotClient telegramBotClient,
            TelegramMessageFormatter telegramMessageFormatter,
            KafkaTopicsProperties kafkaTopicsProperties
    ) {
        this.objectMapper = objectMapper;
        this.telegramBotClient = telegramBotClient;
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.bookingCreated}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeBookingCreated(String payload) {
        BookingCreatedEvent event = readValue(payload, BookingCreatedEvent.class);
        telegramBotClient.sendMessage(telegramMessageFormatter.formatBookingCreatedEvent(event));
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.bookingCanceled}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeBookingCanceled(String payload) {
        BookingCanceledEvent event = readValue(payload, BookingCanceledEvent.class);
        telegramBotClient.sendMessage(telegramMessageFormatter.formatBookingCanceledEvent(event));
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.accommodationCreated}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeAccommodationCreated(String payload) {
        AccommodationCreatedEvent event = readValue(payload, AccommodationCreatedEvent.class);
        telegramBotClient.sendMessage(telegramMessageFormatter.formatAccommodationCreatedEvent(event));
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.paymentSucceeded}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumePaymentSucceeded(String payload) {
        PaymentSucceededEvent event = readValue(payload, PaymentSucceededEvent.class);
        telegramBotClient.sendMessage(telegramMessageFormatter.formatPaymentSucceededEvent(event));
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.bookingExpired}",
            groupId = "${spring.kafka.consumer.group-id:booking-app}-telegram",
            containerFactory = "telegramKafkaListenerContainerFactory"
    )
    public void consumeBookingExpired(String payload) {
        BookingExpiredEvent event = readValue(payload, BookingExpiredEvent.class);
        telegramBotClient.sendMessage(telegramMessageFormatter.formatBookingExpiredEvent(event));
    }

    private <T> T readValue(String payload, Class<T> targetType) {
        try {
            return objectMapper.readValue(payload, targetType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to deserialize Kafka payload to " + targetType.getSimpleName(), exception);
        }
    }
}
