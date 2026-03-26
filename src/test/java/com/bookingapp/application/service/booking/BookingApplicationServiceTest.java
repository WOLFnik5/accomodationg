package com.bookingapp.application.service.booking;

import com.bookingapp.domain.service.dto.CreateBookingCommand;
import com.bookingapp.domain.service.dto.CurrentUser;
import com.bookingapp.domain.service.BookingService;
import com.bookingapp.infrastructure.kafka.KafkaEventPublisher;
import com.bookingapp.domain.repository.AccommodationRepository;
import com.bookingapp.domain.repository.BookingRepository;
import com.bookingapp.domain.repository.PaymentRepository;
import com.bookingapp.infrastructure.security.CurrentUserService;
import com.bookingapp.domain.enums.AccommodationType;
import com.bookingapp.domain.enums.BookingStatus;
import com.bookingapp.domain.enums.UserRole;
import com.bookingapp.domain.exception.BookingConflictException;
import com.bookingapp.domain.exception.InvalidBookingStateException;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingApplicationServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingShouldPersistPendingBookingForCurrentUser() {
        CurrentUser currentUser = new CurrentUser(15L, "customer@example.com", UserRole.CUSTOMER);
        Accommodation accommodation = new Accommodation(
                3L,
                AccommodationType.HOUSE,
                "Warsaw",
                "2 rooms",
                List.of("wifi"),
                BigDecimal.valueOf(120),
                2
        );
        CreateBookingCommand command = new CreateBookingCommand(
                3L,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(8)
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(accommodationRepository.findById(3L)).thenReturn(Optional.of(accommodation));
        when(bookingRepository.existsActiveBookingOverlap(eq(3L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            return new Booking(
                    101L,
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getAccommodationId(),
                    booking.getUserId(),
                    booking.getStatus()
            );
        });

        Booking result = bookingService.createBooking(command);

        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getAccommodationId()).isEqualTo(3L);
        assertThat(result.getUserId()).isEqualTo(15L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
        verify(kafkaEventPublisher).publishBookingCreated(result);
    }

    @Test
    void createBookingShouldRejectOverlap() {
        CurrentUser currentUser = new CurrentUser(15L, "customer@example.com", UserRole.CUSTOMER);
        Accommodation accommodation = new Accommodation(
                3L,
                AccommodationType.HOUSE,
                "Warsaw",
                "2 rooms",
                List.of("wifi"),
                BigDecimal.valueOf(120),
                2
        );
        CreateBookingCommand command = new CreateBookingCommand(
                3L,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(8)
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(accommodationRepository.findById(3L)).thenReturn(Optional.of(accommodation));
        when(bookingRepository.existsActiveBookingOverlap(eq(3L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(command))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("already booked");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBookingShouldMarkBookingAsCanceled() {
        CurrentUser currentUser = new CurrentUser(15L, "customer@example.com", UserRole.CUSTOMER);
        Booking existingBooking = new Booking(
                8L,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                3L,
                15L,
                BookingStatus.PENDING
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.findById(8L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.cancelBooking(8L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELED);
        verify(kafkaEventPublisher).publishBookingCanceled(result);
    }

    @Test
    void cancelBookingShouldRejectSecondCancellation() {
        CurrentUser currentUser = new CurrentUser(15L, "customer@example.com", UserRole.CUSTOMER);
        Booking canceledBooking = new Booking(
                8L,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                3L,
                15L,
                BookingStatus.CANCELED
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.findById(8L)).thenReturn(Optional.of(canceledBooking));

        assertThatThrownBy(() -> bookingService.cancelBooking(8L))
                .isInstanceOf(InvalidBookingStateException.class)
                .hasMessageContaining("already canceled");

        verify(bookingRepository, never()).save(any(Booking.class));
        verify(kafkaEventPublisher, never()).publishBookingCanceled(any(Booking.class));
    }
}
