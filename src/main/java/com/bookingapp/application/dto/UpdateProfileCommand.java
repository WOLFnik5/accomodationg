package com.bookingapp.application.dto;

public record UpdateProfileCommand(
        String email,
        String firstName,
        String lastName
) {
}
