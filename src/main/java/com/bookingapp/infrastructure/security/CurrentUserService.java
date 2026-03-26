package com.bookingapp.infrastructure.security;

import com.bookingapp.domain.service.dto.CurrentUser;

public interface CurrentUserService {

    CurrentUser getCurrentUser();
}
