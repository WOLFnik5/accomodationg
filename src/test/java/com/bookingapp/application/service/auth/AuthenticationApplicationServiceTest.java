package com.bookingapp.application.service.auth;

import com.bookingapp.domain.service.dto.AuthenticationResult;
import com.bookingapp.domain.service.dto.RegisterUserCommand;
import com.bookingapp.domain.service.AuthService;
import com.bookingapp.domain.repository.UserRepository;
import com.bookingapp.infrastructure.security.JwtTokenService;
import com.bookingapp.domain.enums.UserRole;
import com.bookingapp.domain.exception.BusinessValidationException;
import com.bookingapp.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateCustomerWithEncodedPassword() {
        RegisterUserCommand command = new RegisterUserCommand(
                "customer@example.com",
                "John",
                "Doe",
                "raw-password"
        );

        when(userRepository.existsByEmail("customer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
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
        when(jwtTokenService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResult result = authService.register(command);

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("customer@example.com");
        assertThat(result.role()).isEqualTo(UserRole.CUSTOMER);
        assertThat(result.accessToken()).isEqualTo("jwt-token");
        verify(passwordEncoder).encode("raw-password");
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterUserCommand command = new RegisterUserCommand(
                "customer@example.com",
                "John",
                "Doe",
                "raw-password"
        );

        when(userRepository.existsByEmail("customer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(command))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("already exists");
    }
}
