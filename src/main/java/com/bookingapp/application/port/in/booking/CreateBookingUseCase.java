package com.bookingapp.application.port.in.booking;

import com.bookingapp.application.dto.CreateBookingCommand;
import com.bookingapp.domain.model.Booking;

public interface CreateBookingUseCase {

    Booking createBooking(CreateBookingCommand command);
}
