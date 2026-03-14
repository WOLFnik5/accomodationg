package com.bookingapp.adapter.in.web.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateBookingRequest(
        @NotNull @Future LocalDate checkInDate,
        @NotNull @Future LocalDate checkOutDate
) {
}
