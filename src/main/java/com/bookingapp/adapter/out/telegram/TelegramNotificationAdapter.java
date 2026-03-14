package com.bookingapp.adapter.out.telegram;

import com.bookingapp.application.port.out.integration.NotificationPort;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import com.bookingapp.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class TelegramNotificationAdapter implements NotificationPort {

    private final TelegramBotClient telegramBotClient;
    private final TelegramMessageFormatter telegramMessageFormatter;

    public TelegramNotificationAdapter(
            TelegramBotClient telegramBotClient,
            TelegramMessageFormatter telegramMessageFormatter
    ) {
        this.telegramBotClient = telegramBotClient;
        this.telegramMessageFormatter = telegramMessageFormatter;
    }

    @Override
    public void sendMessage(String message) {
        telegramBotClient.sendMessage(message);
    }

    @Override
    public void notifyAccommodationCreated(Accommodation accommodation) {
        sendMessage(telegramMessageFormatter.formatAccommodationCreated(accommodation));
    }

    @Override
    public void notifyBookingCreated(Booking booking) {
        sendMessage(telegramMessageFormatter.formatBookingCreated(booking));
    }

    @Override
    public void notifyBookingCanceled(Booking booking) {
        sendMessage(telegramMessageFormatter.formatBookingCanceled(booking));
    }

    @Override
    public void notifyAccommodationReleased(Booking booking, Accommodation accommodation) {
        sendMessage(telegramMessageFormatter.formatAccommodationReleased(booking, accommodation));
    }

    @Override
    public void notifyPaymentSuccessful(Payment payment) {
        sendMessage(telegramMessageFormatter.formatPaymentSuccessful(payment));
    }

    @Override
    public void notifyNoExpiredBookingsToday() {
        sendMessage("No expired bookings today!");
    }
}
