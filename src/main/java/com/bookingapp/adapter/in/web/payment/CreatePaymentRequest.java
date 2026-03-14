package com.bookingapp.adapter.in.web.payment;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull Long bookingId
) {
}
