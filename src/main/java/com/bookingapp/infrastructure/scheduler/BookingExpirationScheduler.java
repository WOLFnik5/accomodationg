package com.bookingapp.infrastructure.scheduler;

import com.bookingapp.application.dto.ExpireBookingsCommand;
import com.bookingapp.application.port.in.booking.ExpireBookingsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingExpirationScheduler {

    private final ExpireBookingsUseCase expireBookingsUseCase;

    public BookingExpirationScheduler(ExpireBookingsUseCase expireBookingsUseCase) {
        this.expireBookingsUseCase = expireBookingsUseCase;
    }

    @Scheduled(cron = "${app.scheduler.booking-expiration.cron:0 0 1 * * *}")
    public void expireBookingsDaily() {
        expireBookingsUseCase.expireBookings(new ExpireBookingsCommand(LocalDate.now()));
    }
}
