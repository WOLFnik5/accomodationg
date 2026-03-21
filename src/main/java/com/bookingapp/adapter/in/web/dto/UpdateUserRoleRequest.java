package com.bookingapp.adapter.in.web.dto;

import com.bookingapp.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull UserRole role
) {
}
