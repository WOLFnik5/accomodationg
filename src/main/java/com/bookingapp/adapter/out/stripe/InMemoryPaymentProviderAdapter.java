package com.bookingapp.adapter.out.stripe;

import com.bookingapp.application.model.PaymentSession;
import com.bookingapp.application.port.out.integration.PaymentProviderPort;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import com.bookingapp.domain.model.Payment;
import com.bookingapp.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPaymentProviderAdapter implements PaymentProviderPort {

    private final Map<String, Boolean> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, Boolean> successfulSessions = new ConcurrentHashMap<>();

    @Override
    public PaymentSession createPaymentSession(Payment payment, Booking booking, Accommodation accommodation, User user) {
        String sessionId = UUID.randomUUID().toString();
        String sessionUrl = "https://payments.local/checkout/" + sessionId;

        activeSessions.put(sessionId, true);
        successfulSessions.put(sessionId, false);

        return new PaymentSession(
                sessionId,
                sessionUrl,
                payment.getId(),
                payment.getStatus().name()
        );
    }

    @Override
    public boolean isPaymentSuccessful(String sessionId) {
        return successfulSessions.getOrDefault(sessionId, false);
    }

    @Override
    public boolean isPaymentSessionActive(String sessionId) {
        return activeSessions.getOrDefault(sessionId, false);
    }
}
