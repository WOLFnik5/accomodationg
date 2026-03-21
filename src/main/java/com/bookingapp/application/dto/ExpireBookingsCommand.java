package com.bookingapp.application.dto;

import java.time.LocalDate;

public record ExpireBookingsCommand(
        LocalDate businessDate
) {
}
