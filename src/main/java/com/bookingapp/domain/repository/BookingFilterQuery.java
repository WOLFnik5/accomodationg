package com.bookingapp.domain.repository;

import com.bookingapp.domain.model.enums.BookingStatus;

public record BookingFilterQuery(
        Long userId,
        BookingStatus status
) {
}
