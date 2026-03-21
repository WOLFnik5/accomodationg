package com.bookingapp.application.port.in.booking;

import com.bookingapp.application.dto.ExpireBookingsCommand;
import com.bookingapp.application.dto.ExpireBookingsResult;

public interface ExpireBookingsUseCase {

    ExpireBookingsResult expireBookings(ExpireBookingsCommand command);
}
