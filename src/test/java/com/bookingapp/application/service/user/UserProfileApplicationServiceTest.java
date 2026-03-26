package com.bookingapp.application.service.user;

import com.bookingapp.domain.service.dto.CurrentUser;
import com.bookingapp.domain.service.dto.UpdateProfileCommand;
import com.bookingapp.domain.service.UserService;
import com.bookingapp.domain.repository.UserRepository;
import com.bookingapp.infrastructure.security.CurrentUserService;
import com.bookingapp.domain.enums.UserRole;
import com.bookingapp.domain.exception.BusinessValidationException;
import com.bookingapp.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserService userService;

    @Test
    void updateCurrentUserProfileShouldPersistUpdatedValues() {
        CurrentUser currentUser = new CurrentUser(15L, "old@example.com", UserRole.CUSTOMER);
        User existingUser = new User(15L, "old@example.com", "John", "Doe", "encoded", UserRole.CUSTOMER);
        UpdateProfileCommand command = new UpdateProfileCommand("new@example.com", "Jane", "Smith");

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(15L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateCurrentUserProfile(command);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
    }

    @Test
    void updateCurrentUserProfileShouldRejectDuplicateEmail() {
        CurrentUser currentUser = new CurrentUser(15L, "old@example.com", UserRole.CUSTOMER);
        User existingUser = new User(15L, "old@example.com", "John", "Doe", "encoded", UserRole.CUSTOMER);
        UpdateProfileCommand command = new UpdateProfileCommand("taken@example.com", "Jane", "Smith");

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(15L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateCurrentUserProfile(command))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("already exists");
    }
}
