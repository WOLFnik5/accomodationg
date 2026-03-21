package com.bookingapp.application.port.in.accommodation;

import com.bookingapp.application.dto.CreateAccommodationCommand;
import com.bookingapp.domain.model.Accommodation;

public interface CreateAccommodationUseCase {

    Accommodation createAccommodation(CreateAccommodationCommand command);
}
