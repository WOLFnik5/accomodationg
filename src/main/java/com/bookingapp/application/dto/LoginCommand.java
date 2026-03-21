package com.bookingapp.application.dto;

public record LoginCommand(
        String email,
        String password
) {
}
