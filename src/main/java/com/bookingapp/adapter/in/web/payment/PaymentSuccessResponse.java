package com.bookingapp.adapter.in.web.payment;

public record PaymentSuccessResponse(
        String message,
        PaymentResponse payment
) {
}
