package com.bookingapp.application.port.in.auth;

import com.bookingapp.application.dto.AuthenticationResult;
import com.bookingapp.application.dto.LoginCommand;

public interface LoginUseCase {

    AuthenticationResult login(LoginCommand command);
}
