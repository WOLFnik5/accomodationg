package com.bookingapp.adapter.in.web.dto;

import com.bookingapp.domain.enums.UserRole;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String email,
        UserRole role
) {
}
