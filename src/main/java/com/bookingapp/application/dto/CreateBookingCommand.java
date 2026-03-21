package com.bookingapp.application.dto;

import java.time.LocalDate;

public record CreateBookingCommand(
        Long accommodationId,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
}
