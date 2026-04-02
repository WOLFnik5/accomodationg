package com.bookingapp.service;

import java.util.List;

public record BookingExpirationResult(
        int expiredCount,
        List<Long> expiredBookingIds
) {
}
