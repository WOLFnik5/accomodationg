package com.bookingapp.application.dto.event;

import java.time.Instant;

public record BookingCanceledEvent(
        Long bookingId,
        Long accommodationId,
        Long userId,
        Instant canceledAt
) {
}
