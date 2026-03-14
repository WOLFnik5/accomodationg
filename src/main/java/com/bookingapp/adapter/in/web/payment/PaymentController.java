package com.bookingapp.adapter.in.web.payment;

import com.bookingapp.application.model.PaymentSession;
import com.bookingapp.application.port.in.payment.CreatePaymentSessionUseCase;
import com.bookingapp.application.port.in.payment.GetPaymentsUseCase;
import com.bookingapp.application.port.in.payment.HandlePaymentCancelUseCase;
import com.bookingapp.application.port.in.payment.HandlePaymentSuccessUseCase;
import com.bookingapp.domain.model.Payment;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePaymentSessionUseCase createPaymentSessionUseCase;
    private final GetPaymentsUseCase getPaymentsUseCase;
    private final HandlePaymentSuccessUseCase handlePaymentSuccessUseCase;
    private final HandlePaymentCancelUseCase handlePaymentCancelUseCase;
    private final PaymentWebMapper paymentWebMapper;

    public PaymentController(
            CreatePaymentSessionUseCase createPaymentSessionUseCase,
            GetPaymentsUseCase getPaymentsUseCase,
            HandlePaymentSuccessUseCase handlePaymentSuccessUseCase,
            HandlePaymentCancelUseCase handlePaymentCancelUseCase,
            PaymentWebMapper paymentWebMapper
    ) {
        this.createPaymentSessionUseCase = createPaymentSessionUseCase;
        this.getPaymentsUseCase = getPaymentsUseCase;
        this.handlePaymentSuccessUseCase = handlePaymentSuccessUseCase;
        this.handlePaymentCancelUseCase = handlePaymentCancelUseCase;
        this.paymentWebMapper = paymentWebMapper;
    }

    @GetMapping
    public List<PaymentResponse> getPayments(
            @RequestParam(name = "user_id", required = false) Long userId
    ) {
        return getPaymentsUseCase.getPayments(paymentWebMapper.toFilterQuery(userId)).stream()
                .map(paymentWebMapper::toResponse)
                .toList();
    }

    @PostMapping
    public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentSession paymentSession = createPaymentSessionUseCase.createPaymentSession(
                paymentWebMapper.toCreatePaymentSessionCommand(request)
        );

        return paymentWebMapper.toResponse(
                paymentSession.paymentId(),
                paymentSession.sessionId(),
                paymentSession.sessionUrl(),
                paymentSession.status(),
                paymentSession.amountToPay(),
                paymentSession.bookingId()
        );
    }

    @GetMapping("/success")
    public PaymentSuccessResponse handlePaymentSuccess(
            @RequestParam(name = "session_id") String sessionId
    ) {
        Payment payment = handlePaymentSuccessUseCase.handlePaymentSuccess(sessionId);
        return paymentWebMapper.toSuccessResponse(payment);
    }

    @GetMapping("/cancel")
    public PaymentCancelResponse handlePaymentCancel(
            @RequestParam(name = "session_id") String sessionId
    ) {
        return paymentWebMapper.toCancelResponse(handlePaymentCancelUseCase.handlePaymentCancel(sessionId));
    }
}
