package com.bookingapp.adapter.out.telegram;

import com.bookingapp.application.port.out.integration.NotificationPort;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import com.bookingapp.domain.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void notifyAccommodationCreated(Accommodation accommodation) {
        LOGGER.info("Accommodation created notification prepared. accommodationId={}", accommodation.getId());
    }

    @Override
    public void notifyBookingCreated(Booking booking) {
        LOGGER.info("Booking created notification prepared. bookingId={}", booking.getId());
    }

    @Override
    public void notifyBookingCanceled(Booking booking) {
        LOGGER.info("Booking canceled notification prepared. bookingId={}", booking.getId());
    }

    @Override
    public void notifyAccommodationReleased(Booking booking, Accommodation accommodation) {
        LOGGER.info(
                "Accommodation released notification prepared. bookingId={}, accommodationId={}",
                booking.getId(),
                accommodation.getId()
        );
    }

    @Override
    public void notifyPaymentSuccessful(Payment payment) {
        LOGGER.info("Payment successful notification prepared. paymentId={}", payment.getId());
    }

    @Override
    public void notifyNoExpiredBookingsToday() {
        LOGGER.info("No expired bookings today notification prepared.");
    }
}
