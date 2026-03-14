package com.bookingapp.adapter.in.web.booking;

import com.bookingapp.application.model.BookingFilterQuery;
import com.bookingapp.application.model.CreateBookingCommand;
import com.bookingapp.application.model.UpdateBookingCommand;
import com.bookingapp.domain.enums.BookingStatus;
import com.bookingapp.domain.exception.BusinessValidationException;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingWebMapper {

    public CreateBookingCommand toCreateCommand(CreateBookingRequest request) {
        return new CreateBookingCommand(
                request.accommodationId(),
                request.checkInDate(),
                request.checkOutDate()
        );
    }

    public UpdateBookingCommand toUpdateCommand(Long bookingId, UpdateBookingRequest request) {
        return new UpdateBookingCommand(
                bookingId,
                request.checkInDate(),
                request.checkOutDate()
        );
    }

    public UpdateBookingCommand toPatchCommand(Long bookingId, PatchBookingRequest request, Booking currentBooking) {
        LocalDate checkInDate = request.checkInDate() != null ? request.checkInDate() : currentBooking.getCheckInDate();
        LocalDate checkOutDate = request.checkOutDate() != null ? request.checkOutDate() : currentBooking.getCheckOutDate();

        if (checkInDate == null || checkOutDate == null) {
            throw new BusinessValidationException("Booking dates must not be null");
        }

        return new UpdateBookingCommand(bookingId, checkInDate, checkOutDate);
    }

    public BookingFilterQuery toFilterQuery(Long userId, BookingStatus status) {
        return new BookingFilterQuery(userId, status);
    }

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getAccommodationId(),
                booking.getUserId(),
                booking.getStatus()
        );
    }

    public BookingDetailResponse toDetailResponse(Booking booking, Accommodation accommodation) {
        return new BookingDetailResponse(
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getAccommodationId(),
                booking.getUserId(),
                booking.getStatus(),
                new AccommodationSummaryResponse(
                        accommodation.getId(),
                        accommodation.getType(),
                        accommodation.getLocation(),
                        accommodation.getSize()
                )
        );
    }
}
