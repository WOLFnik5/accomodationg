package com.bookingapp.web.dto;

import jakarta.validation.constraints.Email;

public record PatchCurrentUserRequest(
        @Email String email,
        String firstName,
        String lastName
) {
}
