package com.bookingapp.application.service.payment;

import com.bookingapp.application.model.CreatePaymentSessionCommand;
import com.bookingapp.application.model.CurrentUser;
import com.bookingapp.application.model.PaymentSession;
import com.bookingapp.application.port.out.integration.EventPublisherPort;
import com.bookingapp.application.port.out.integration.PaymentProviderPort;
import com.bookingapp.application.port.out.persistence.AccommodationRepositoryPort;
import com.bookingapp.application.port.out.persistence.BookingRepositoryPort;
import com.bookingapp.application.port.out.persistence.PaymentRepositoryPort;
import com.bookingapp.application.port.out.persistence.UserRepositoryPort;
import com.bookingapp.application.port.out.security.CurrentUserProviderPort;
import com.bookingapp.domain.enums.AccommodationType;
import com.bookingapp.domain.enums.BookingStatus;
import com.bookingapp.domain.enums.PaymentStatus;
import com.bookingapp.domain.enums.UserRole;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import com.bookingapp.domain.model.Payment;
import com.bookingapp.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceTest {

    @Mock
    private PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    private BookingRepositoryPort bookingRepositoryPort;

    @Mock
    private AccommodationRepositoryPort accommodationRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private CurrentUserProviderPort currentUserProviderPort;

    @Mock
    private PaymentProviderPort paymentProviderPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private PaymentApplicationService paymentApplicationService;

    @Test
    void createPaymentSessionShouldCalculateAmountFromBookingDaysAndRate() {
        Booking booking = new Booking(
                11L,
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 13),
                3L,
                15L,
                BookingStatus.PENDING
        );
        Accommodation accommodation = new Accommodation(
                3L,
                AccommodationType.APARTMENT,
                "Warsaw",
                "Studio",
                List.of("wifi"),
                BigDecimal.valueOf(150),
                1
        );
        User bookingOwner = new User(15L, "customer@example.com", "John", "Doe", "encoded", UserRole.CUSTOMER);
        CurrentUser currentUser = new CurrentUser(15L, "customer@example.com", UserRole.CUSTOMER);

        when(currentUserProviderPort.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepositoryPort.findById(11L)).thenReturn(Optional.of(booking));
        when(accommodationRepositoryPort.findById(3L)).thenReturn(Optional.of(accommodation));
        when(userRepositoryPort.findById(15L)).thenReturn(Optional.of(bookingOwner));
        when(paymentRepositoryPort.findByBookingId(11L)).thenReturn(Optional.empty());
        when(paymentProviderPort.createPaymentSession(any(Payment.class), any(Booking.class), any(Accommodation.class), any(User.class)))
                .thenReturn(new PaymentSession(
                        "sess_123",
                        "https://checkout.example/sess_123",
                        null,
                        PaymentStatus.PENDING.name(),
                        11L,
                        BigDecimal.valueOf(450)
                ));
        when(paymentRepositoryPort.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            return new Payment(
                    100L,
                    payment.getStatus(),
                    payment.getBookingId(),
                    payment.getSessionUrl(),
                    payment.getSessionId(),
                    payment.getAmountToPay()
            );
        });

        PaymentSession result = paymentApplicationService.createPaymentSession(new CreatePaymentSessionCommand(11L));

        assertThat(result.paymentId()).isEqualTo(100L);
        assertThat(result.amountToPay()).isEqualByComparingTo("450");

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepositoryPort).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getAmountToPay()).isEqualByComparingTo("450");
        assertThat(paymentCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void handlePaymentSuccessShouldMarkPaymentAsPaidAndPublishEvent() {
        Payment pendingPayment = new Payment(
                100L,
                PaymentStatus.PENDING,
                11L,
                "https://checkout.example/sess_123",
                "sess_123",
                BigDecimal.valueOf(450)
        );

        when(paymentRepositoryPort.findBySessionId("sess_123")).thenReturn(Optional.of(pendingPayment));
        when(paymentProviderPort.isPaymentSuccessful("sess_123")).thenReturn(true);
        when(paymentRepositoryPort.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentApplicationService.handlePaymentSuccess("sess_123");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
        verify(eventPublisherPort).publishPaymentSucceeded(result);
    }
}
