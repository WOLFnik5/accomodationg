package com.bookingapp.adapter.in.web.booking;

import jakarta.validation.constraints.Future;

import java.time.LocalDate;

public record PatchBookingRequest(
        @Future LocalDate checkInDate,
        @Future LocalDate checkOutDate
) {
}
