package com.bookingapp.application.port.in.booking;

import com.bookingapp.application.dto.BookingFilterQuery;
import com.bookingapp.domain.model.Booking;

import java.util.List;

public interface ListBookingsUseCase {

    List<Booking> listBookings(BookingFilterQuery query);
}
