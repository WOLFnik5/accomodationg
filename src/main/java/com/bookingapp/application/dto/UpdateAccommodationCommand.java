package com.bookingapp.application.dto;

import com.bookingapp.domain.enums.AccommodationType;

import java.math.BigDecimal;
import java.util.List;

public record UpdateAccommodationCommand(
        Long accommodationId,
        AccommodationType type,
        String location,
        String size,
        List<String> amenities,
        BigDecimal dailyRate,
        Integer availability
) {
}
