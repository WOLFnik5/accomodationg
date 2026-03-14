package com.bookingapp.application.model.event;

import java.time.Instant;

public record BookingCanceledEvent(
        Long bookingId,
        Long accommodationId,
        Long userId,
        Instant canceledAt
) {
}
