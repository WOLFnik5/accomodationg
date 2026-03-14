package com.bookingapp.adapter.in.web.booking;

import com.bookingapp.domain.enums.AccommodationType;

public record AccommodationSummaryResponse(
        Long id,
        AccommodationType type,
        String location,
        String size
) {
}
