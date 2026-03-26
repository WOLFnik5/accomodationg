package com.bookingapp.web.dto;

public record PaymentSuccessResponse(
        String message,
        PaymentResponse payment
) {
}
