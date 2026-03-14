package com.bookingapp.adapter.out.kafka;

import com.bookingapp.application.port.out.integration.EventPublisherPort;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import com.bookingapp.domain.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventPublisherAdapter implements EventPublisherPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventPublisherAdapter.class);

    @Override
    public void publishAccommodationCreated(Accommodation accommodation) {
        LOGGER.info("Accommodation created event queued for publication. accommodationId={}", accommodation.getId());
    }

    @Override
    public void publishBookingCreated(Booking booking) {
        LOGGER.info("Booking created event queued for publication. bookingId={}", booking.getId());
    }

    @Override
    public void publishBookingCanceled(Booking booking) {
        LOGGER.info("Booking canceled event queued for publication. bookingId={}", booking.getId());
    }

    @Override
    public void publishBookingExpired(Booking booking) {
        LOGGER.info("Booking expired event queued for publication. bookingId={}", booking.getId());
    }

    @Override
    public void publishPaymentSucceeded(Payment payment) {
        LOGGER.info("Payment succeeded event queued for publication. paymentId={}", payment.getId());
    }
}
