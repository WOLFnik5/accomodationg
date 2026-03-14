package com.bookingapp.adapter.in.web.support;

import com.bookingapp.adapter.out.persistence.repository.JpaAccommodationRepository;
import com.bookingapp.adapter.out.persistence.repository.JpaBookingRepository;
import com.bookingapp.adapter.out.persistence.repository.JpaPaymentRepository;
import com.bookingapp.adapter.out.persistence.repository.JpaUserRepository;
import com.bookingapp.application.port.out.integration.EventPublisherPort;
import com.bookingapp.application.port.out.integration.PaymentProviderPort;
import com.bookingapp.application.port.out.persistence.AccommodationRepositoryPort;
import com.bookingapp.application.port.out.persistence.BookingRepositoryPort;
import com.bookingapp.application.port.out.persistence.PaymentRepositoryPort;
import com.bookingapp.application.port.out.persistence.UserRepositoryPort;
import com.bookingapp.domain.enums.AccommodationType;
import com.bookingapp.domain.enums.BookingStatus;
import com.bookingapp.domain.enums.PaymentStatus;
import com.bookingapp.domain.enums.UserRole;
import com.bookingapp.domain.model.Accommodation;
import com.bookingapp.domain.model.Booking;
import com.bookingapp.domain.model.Payment;
import com.bookingapp.domain.model.User;
import com.bookingapp.infrastructure.security.JwtTokenService;
import com.bookingapp.support.PostgreSqlIntegrationTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ControllerIntegrationTestConfiguration.class)
public abstract class AbstractControllerIntegrationTest extends PostgreSqlIntegrationTestSupport {

    protected static final String DEFAULT_PASSWORD = "Password123!";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenService jwtTokenService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepositoryPort userRepositoryPort;

    @Autowired
    protected AccommodationRepositoryPort accommodationRepositoryPort;

    @Autowired
    protected BookingRepositoryPort bookingRepositoryPort;

    @Autowired
    protected PaymentRepositoryPort paymentRepositoryPort;

    @Autowired
    protected JpaUserRepository jpaUserRepository;

    @Autowired
    protected JpaAccommodationRepository jpaAccommodationRepository;

    @Autowired
    protected JpaBookingRepository jpaBookingRepository;

    @Autowired
    protected JpaPaymentRepository jpaPaymentRepository;

    @Autowired
    protected EventPublisherPort eventPublisherPort;

    @Autowired
    protected PaymentProviderPort paymentProviderPort;

    @BeforeEach
    void resetExternalMocks() {
        Mockito.reset(eventPublisherPort, paymentProviderPort);
    }

    @AfterEach
    void cleanDatabase() {
        jpaPaymentRepository.deleteAllInBatch();
        jpaBookingRepository.deleteAllInBatch();
        jpaAccommodationRepository.deleteAllInBatch();
        jpaUserRepository.deleteAllInBatch();
    }

    protected User persistAdmin(String email) {
        return persistUser(email, "Admin", "User", DEFAULT_PASSWORD, UserRole.ADMIN);
    }

    protected User persistCustomer(String email) {
        return persistUser(email, "Test", "Customer", DEFAULT_PASSWORD, UserRole.CUSTOMER);
    }

    protected User persistUser(String email, String firstName, String lastName, String rawPassword, UserRole role) {
        return userRepositoryPort.save(
                User.createNew(email, firstName, lastName, passwordEncoder.encode(rawPassword), role)
        );
    }

    protected Accommodation persistAccommodation(
            AccommodationType type,
            String location,
            String size,
            List<String> amenities,
            BigDecimal dailyRate,
            int availability
    ) {
        return accommodationRepositoryPort.save(
                Accommodation.createNew(type, location, size, amenities, dailyRate, availability)
        );
    }

    protected Booking persistBooking(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Long accommodationId,
            Long userId,
            BookingStatus status
    ) {
        return bookingRepositoryPort.save(
                new Booking(null, checkInDate, checkOutDate, accommodationId, userId, status)
        );
    }

    protected Payment persistPayment(
            PaymentStatus status,
            Long bookingId,
            String sessionUrl,
            String sessionId,
            BigDecimal amountToPay
    ) {
        return paymentRepositoryPort.save(
                new Payment(null, status, bookingId, sessionUrl, sessionId, amountToPay)
        );
    }

    protected String authorizationHeader(User user) {
        return "Bearer " + jwtTokenService.generateToken(user);
    }

    protected String asJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    protected LocalDate futureDate(int daysFromNow) {
        return LocalDate.now().plusDays(daysFromNow);
    }
}
