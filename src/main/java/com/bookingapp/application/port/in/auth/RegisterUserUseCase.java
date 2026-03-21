package com.bookingapp.application.port.in.auth;

import com.bookingapp.application.dto.AuthenticationResult;
import com.bookingapp.application.dto.RegisterUserCommand;

public interface RegisterUserUseCase {

    AuthenticationResult register(RegisterUserCommand command);
}
