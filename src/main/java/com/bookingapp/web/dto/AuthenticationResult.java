package com.bookingapp.web.dto;

import com.bookingapp.domain.model.enums.UserRole;

public record AuthenticationResult(
        String accessToken,
        Long userId,
        String email,
        UserRole role
) {
}
