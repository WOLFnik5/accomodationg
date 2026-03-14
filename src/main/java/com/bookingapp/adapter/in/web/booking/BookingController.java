package com.bookingapp.adapter.in.web.booking;

import com.bookingapp.application.port.in.accommodation.GetAccommodationByIdUseCase;
import com.bookingapp.application.port.in.booking.CancelBookingUseCase;
import com.bookingapp.application.port.in.booking.CreateBookingUseCase;
import com.bookingapp.application.port.in.booking.GetBookingByIdUseCase;
import com.bookingapp.application.port.in.booking.ListBookingsUseCase;
import com.bookingapp.application.port.in.booking.ListMyBookingsUseCase;
import com.bookingapp.application.port.in.booking.UpdateBookingUseCase;
import com.bookingapp.domain.enums.BookingStatus;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final GetBookingByIdUseCase getBookingByIdUseCase;
    private final ListBookingsUseCase listBookingsUseCase;
    private final ListMyBookingsUseCase listMyBookingsUseCase;
    private final UpdateBookingUseCase updateBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final GetAccommodationByIdUseCase getAccommodationByIdUseCase;
    private final BookingWebMapper bookingWebMapper;

    public BookingController(
            CreateBookingUseCase createBookingUseCase,
            GetBookingByIdUseCase getBookingByIdUseCase,
            ListBookingsUseCase listBookingsUseCase,
            ListMyBookingsUseCase listMyBookingsUseCase,
            UpdateBookingUseCase updateBookingUseCase,
            CancelBookingUseCase cancelBookingUseCase,
            GetAccommodationByIdUseCase getAccommodationByIdUseCase,
            BookingWebMapper bookingWebMapper
    ) {
        this.createBookingUseCase = createBookingUseCase;
        this.getBookingByIdUseCase = getBookingByIdUseCase;
        this.listBookingsUseCase = listBookingsUseCase;
        this.listMyBookingsUseCase = listMyBookingsUseCase;
        this.updateBookingUseCase = updateBookingUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.getAccommodationByIdUseCase = getAccommodationByIdUseCase;
        this.bookingWebMapper = bookingWebMapper;
    }

    @PostMapping
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        Booking createdBooking = createBookingUseCase.createBooking(bookingWebMapper.toCreateCommand(request));
        return bookingWebMapper.toResponse(createdBooking);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponse> listBookings(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "status", required = false) BookingStatus status
    ) {
        return listBookingsUseCase.listBookings(bookingWebMapper.toFilterQuery(userId, status)).stream()
                .map(bookingWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/my")
    public List<BookingResponse> listMyBookings() {
        return listMyBookingsUseCase.listMyBookings().stream()
                .map(bookingWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookingDetailResponse getBookingById(@PathVariable Long id) {
        Booking booking = getBookingByIdUseCase.getBookingById(id);
        Accommodation accommodation = getAccommodationByIdUseCase.getAccommodationById(booking.getAccommodationId());
        return bookingWebMapper.toDetailResponse(booking, accommodation);
    }

    @PutMapping("/{id}")
    public BookingResponse updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request
    ) {
        Booking updatedBooking = updateBookingUseCase.updateBooking(
                bookingWebMapper.toUpdateCommand(id, request)
        );
        return bookingWebMapper.toResponse(updatedBooking);
    }

    @PatchMapping("/{id}")
    public BookingResponse patchBooking(
            @PathVariable Long id,
            @Valid @RequestBody PatchBookingRequest request
    ) {
        Booking currentBooking = getBookingByIdUseCase.getBookingById(id);
        Booking updatedBooking = updateBookingUseCase.updateBooking(
                bookingWebMapper.toPatchCommand(id, request, currentBooking)
        );
        return bookingWebMapper.toResponse(updatedBooking);
    }

    @DeleteMapping("/{id}")
    public BookingResponse cancelBooking(@PathVariable Long id) {
        Booking canceledBooking = cancelBookingUseCase.cancelBooking(id);
        return bookingWebMapper.toResponse(canceledBooking);
    }
}
