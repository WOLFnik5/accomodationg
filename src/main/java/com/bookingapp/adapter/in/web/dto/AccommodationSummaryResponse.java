package com.bookingapp.adapter.in.web.dto;

import com.bookingapp.domain.enums.AccommodationType;

public record AccommodationSummaryResponse(
        Long id,
        AccommodationType type,
        String location,
        String size
) {
}
