package com.bookingapp.application.dto.event;

import java.time.Instant;

public record BookingExpiredEvent(
        Long bookingId,
        Long accommodationId,
        Long userId,
        Instant expiredAt
) {
}
