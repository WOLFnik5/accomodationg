package com.bookingapp.application.service.payment;

import com.bookingapp.domain.service.dto.CreatePaymentSessionCommand;
import com.bookingapp.domain.service.dto.CurrentUser;
import com.bookingapp.domain.service.dto.PaymentSession;
import com.bookingapp.domain.service.PaymentService;
import com.bookingapp.infrastructure.kafka.KafkaEventPublisher;
import com.bookingapp.domain.repository.AccommodationRepository;
import com.bookingapp.domain.repository.BookingRepository;
import com.bookingapp.domain.repository.PaymentRepository;
import com.bookingapp.domain.repository.UserRepository;
import com.bookingapp.infrastructure.security.CurrentUserService;
import com.bookingapp.infrastructure.stripe.StripePaymentProvider;
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
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private StripePaymentProvider stripePaymentProvider;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

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

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.findById(11L)).thenReturn(Optional.of(booking));
        when(accommodationRepository.findById(3L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findById(15L)).thenReturn(Optional.of(bookingOwner));
        when(paymentRepository.findByBookingId(11L)).thenReturn(Optional.empty());
        when(stripePaymentProvider.createPaymentSession(any(Payment.class), any(Booking.class), any(Accommodation.class), any(User.class)))
                .thenReturn(new PaymentSession(
                        "sess_123",
                        "https://checkout.example/sess_123",
                        null,
                        PaymentStatus.PENDING.name(),
                        11L,
                        BigDecimal.valueOf(450)
                ));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
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

        PaymentSession result = paymentService.createPaymentSession(new CreatePaymentSessionCommand(11L));

        assertThat(result.paymentId()).isEqualTo(100L);
        assertThat(result.amountToPay()).isEqualByComparingTo("450");

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
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

        when(paymentRepository.findBySessionId("sess_123")).thenReturn(Optional.of(pendingPayment));
        when(stripePaymentProvider.isPaymentSuccessful("sess_123")).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.handlePaymentSuccess("sess_123");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
        verify(kafkaEventPublisher).publishPaymentSucceeded(result);
    }
}
