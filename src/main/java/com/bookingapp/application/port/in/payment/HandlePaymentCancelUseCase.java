package com.bookingapp.application.port.in.payment;

import com.bookingapp.application.dto.PaymentCancelResult;

public interface HandlePaymentCancelUseCase {

    PaymentCancelResult handlePaymentCancel(String sessionId);
}
