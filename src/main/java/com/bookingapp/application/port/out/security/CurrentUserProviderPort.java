package com.bookingapp.application.port.out.security;

import com.bookingapp.application.dto.CurrentUser;

public interface CurrentUserProviderPort {

    CurrentUser getCurrentUser();
}
