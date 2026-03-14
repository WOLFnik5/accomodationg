package com.bookingapp.application.service.accommodation;

import com.bookingapp.application.model.CreateAccommodationCommand;
import com.bookingapp.application.model.UpdateAccommodationCommand;
import com.bookingapp.application.port.out.integration.EventPublisherPort;
import com.bookingapp.application.port.out.persistence.AccommodationRepositoryPort;
import com.bookingapp.domain.enums.AccommodationType;
import com.bookingapp.domain.model.Accommodation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccommodationApplicationServiceTest {

    @Mock
    private AccommodationRepositoryPort accommodationRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private AccommodationApplicationService accommodationApplicationService;

    @Test
    void createAccommodationShouldPersistAndPublishEvent() {
        CreateAccommodationCommand command = new CreateAccommodationCommand(
                AccommodationType.HOUSE,
                "Warsaw",
                "2 rooms",
                List.of("wifi", "parking"),
                BigDecimal.valueOf(125),
                3
        );

        when(accommodationRepositoryPort.save(any(Accommodation.class))).thenAnswer(invocation -> {
            Accommodation accommodation = invocation.getArgument(0);
            return new Accommodation(
                    1L,
                    accommodation.getType(),
                    accommodation.getLocation(),
                    accommodation.getSize(),
                    accommodation.getAmenities(),
                    accommodation.getDailyRate(),
                    accommodation.getAvailability()
            );
        });

        Accommodation result = accommodationApplicationService.createAccommodation(command);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLocation()).isEqualTo("Warsaw");
        assertThat(result.getAmenities()).containsExactly("wifi", "parking");
        verify(eventPublisherPort).publishAccommodationCreated(result);
    }

    @Test
    void updateAccommodationShouldReplaceExistingDetails() {
        Accommodation existingAccommodation = new Accommodation(
                7L,
                AccommodationType.APARTMENT,
                "Gdansk",
                "Studio",
                List.of("wifi"),
                BigDecimal.valueOf(90),
                1
        );
        UpdateAccommodationCommand command = new UpdateAccommodationCommand(
                7L,
                AccommodationType.CONDO,
                "Krakow",
                "3 rooms",
                List.of("wifi", "sauna"),
                BigDecimal.valueOf(175),
                4
        );

        when(accommodationRepositoryPort.findById(7L)).thenReturn(Optional.of(existingAccommodation));
        when(accommodationRepositoryPort.save(any(Accommodation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Accommodation result = accommodationApplicationService.updateAccommodation(command);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getType()).isEqualTo(AccommodationType.CONDO);
        assertThat(result.getLocation()).isEqualTo("Krakow");
        assertThat(result.getDailyRate()).isEqualByComparingTo("175");
        assertThat(result.getAvailability()).isEqualTo(4);
    }
}
