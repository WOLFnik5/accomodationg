package com.bookingapp.application.service.booking;

import com.bookingapp.application.dto.CreateBookingCommand;
import com.bookingapp.application.dto.CurrentUser;
import com.bookingapp.application.port.out.integration.EventPublisherPort;
import com.bookingapp.application.port.out.persistence.AccommodationRepositoryPort;
import com.bookingapp.application.port.out.persistence.BookingRepositoryPort;
import com.bookingapp.application.port.out.persistence.PaymentRepositoryPort;
import com.bookingapp.application.port.out.security.CurrentUserProviderPort;
import com.bookingapp.application.usecase.booking.BookingApplicationService;
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
    private BookingRepositoryPort bookingRepositoryPort;

    @Mock
    private AccommodationRepositoryPort accommodationRepositoryPort;

    @Mock
    private PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    private CurrentUserProviderPort currentUserProviderPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private BookingApplicationService bookingApplicationService;

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

        when(currentUserProviderPort.getCurrentUser()).thenReturn(currentUser);
        when(accommodationRepositoryPort.findById(3L)).thenReturn(Optional.of(accommodation));
        when(bookingRepositoryPort.existsActiveBookingOverlap(eq(3L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(false);
        when(bookingRepositoryPort.save(any(Booking.class))).thenAnswer(invocation -> {
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

        Booking result = bookingApplicationService.createBooking(command);

        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getAccommodationId()).isEqualTo(3L);
        assertThat(result.getUserId()).isEqualTo(15L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
        verify(eventPublisherPort).publishBookingCreated(result);
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

        when(currentUserProviderPort.getCurrentUser()).thenReturn(currentUser);
        when(accommodationRepositoryPort.findById(3L)).thenReturn(Optional.of(accommodation));
        when(bookingRepositoryPort.existsActiveBookingOverlap(eq(3L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingApplicationService.createBooking(command))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("already booked");

        verify(bookingRepositoryPort, never()).save(any(Booking.class));
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

        when(currentUserProviderPort.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepositoryPort.findById(8L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepositoryPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingApplicationService.cancelBooking(8L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELED);
        verify(eventPublisherPort).publishBookingCanceled(result);
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

        when(currentUserProviderPort.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepositoryPort.findById(8L)).thenReturn(Optional.of(canceledBooking));

        assertThatThrownBy(() -> bookingApplicationService.cancelBooking(8L))
                .isInstanceOf(InvalidBookingStateException.class)
                .hasMessageContaining("already canceled");

        verify(bookingRepositoryPort, never()).save(any(Booking.class));
        verify(eventPublisherPort, never()).publishBookingCanceled(any(Booking.class));
    }
}
