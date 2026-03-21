package com.bookingapp.application.dto;

public record RegisterUserCommand(
        String email,
        String firstName,
        String lastName,
        String password
) {
}
