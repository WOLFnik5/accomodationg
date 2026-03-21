package com.bookingapp.adapter.in.web.dto;

import com.bookingapp.domain.enums.UserRole;

public record UserProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        UserRole role
) {
}
