package com.bookingapp.application.service.auth;

import com.bookingapp.application.dto.AuthenticationResult;
import com.bookingapp.application.dto.RegisterUserCommand;
import com.bookingapp.application.port.out.persistence.UserRepositoryPort;
import com.bookingapp.application.port.out.security.PasswordEncoderPort;
import com.bookingapp.application.port.out.security.TokenProviderPort;
import com.bookingapp.application.usecase.auth.AuthenticationApplicationService;
import com.bookingapp.domain.enums.UserRole;
import com.bookingapp.domain.exception.BusinessValidationException;
import com.bookingapp.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @InjectMocks
    private AuthenticationApplicationService authenticationApplicationService;

    @Test
    void registerShouldCreateCustomerWithEncodedPassword() {
        RegisterUserCommand command = new RegisterUserCommand(
                "customer@example.com",
                "John",
                "Doe",
                "raw-password"
        );

        when(userRepositoryPort.existsByEmail("customer@example.com")).thenReturn(false);
        when(passwordEncoderPort.encode("raw-password")).thenReturn("encoded-password");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(
                    1L,
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPassword(),
                    user.getRole()
            );
        });
        when(tokenProviderPort.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResult result = authenticationApplicationService.register(command);

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("customer@example.com");
        assertThat(result.role()).isEqualTo(UserRole.CUSTOMER);
        assertThat(result.accessToken()).isEqualTo("jwt-token");
        verify(passwordEncoderPort).encode("raw-password");
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterUserCommand command = new RegisterUserCommand(
                "customer@example.com",
                "John",
                "Doe",
                "raw-password"
        );

        when(userRepositoryPort.existsByEmail("customer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authenticationApplicationService.register(command))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("already exists");
    }
}
