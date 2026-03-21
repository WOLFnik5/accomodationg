package com.bookingapp.application.dto;

import java.math.BigDecimal;

public record PaymentSession(
        String sessionId,
        String sessionUrl,
        Long paymentId,
        String status,
        Long bookingId,
        BigDecimal amountToPay
) {
}
