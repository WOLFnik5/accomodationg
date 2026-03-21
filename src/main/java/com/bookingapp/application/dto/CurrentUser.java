package com.bookingapp.application.dto;

import com.bookingapp.domain.enums.UserRole;

public record CurrentUser(
        Long id,
        String email,
        UserRole role
) {
}
