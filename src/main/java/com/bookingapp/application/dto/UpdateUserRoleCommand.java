package com.bookingapp.application.dto;

import com.bookingapp.domain.enums.UserRole;

public record UpdateUserRoleCommand(
        Long userId,
        UserRole role
) {
}
