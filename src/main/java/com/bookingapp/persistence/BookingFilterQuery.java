package com.bookingapp.persistence;

import com.bookingapp.domain.model.enums.BookingStatus;

public record BookingFilterQuery(
        Long userId,
        BookingStatus status
) {
}
