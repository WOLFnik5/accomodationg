package com.bookingapp.application.port.in.payment;

import com.bookingapp.application.dto.CreatePaymentSessionCommand;
import com.bookingapp.application.dto.PaymentSession;

public interface CreatePaymentSessionUseCase {

    PaymentSession createPaymentSession(CreatePaymentSessionCommand command);
}
