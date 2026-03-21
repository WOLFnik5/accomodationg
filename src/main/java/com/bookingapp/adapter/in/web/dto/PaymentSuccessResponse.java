package com.bookingapp.adapter.in.web.dto;

public record PaymentSuccessResponse(
        String message,
        PaymentResponse payment
) {
}
