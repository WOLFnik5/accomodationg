package com.bookingapp.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull Long bookingId
) {
}
