package com.bookingapp.adapter.in.web.dto;

import com.bookingapp.domain.enums.PaymentStatus;

public record PaymentCancelResponse(
        String message,
        boolean canBeCompletedLater,
        Long paymentId,
        PaymentStatus paymentStatus,
        String sessionId,
        String sessionUrl
) {
}
