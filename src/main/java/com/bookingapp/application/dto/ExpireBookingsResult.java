package com.bookingapp.application.dto;

import java.util.List;

public record ExpireBookingsResult(
        int expiredCount,
        List<Long> expiredBookingIds
) {
}
