package com.bookingapp.application.port.in.accommodation;

import com.bookingapp.application.dto.UpdateAccommodationCommand;
import com.bookingapp.domain.model.Accommodation;

public interface UpdateAccommodationUseCase {

    Accommodation updateAccommodation(UpdateAccommodationCommand command);
}
