package com.bookingapp.application.dto;

import java.time.LocalDate;

public record UpdateBookingCommand(
        Long bookingId,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
}
