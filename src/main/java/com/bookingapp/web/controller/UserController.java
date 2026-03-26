package com.bookingapp.web.controller;

import com.bookingapp.web.dto.PatchCurrentUserRequest;
import com.bookingapp.web.dto.UpdateCurrentUserRequest;
import com.bookingapp.web.dto.UpdateUserRoleRequest;
import com.bookingapp.web.dto.UserProfileResponse;
import com.bookingapp.web.mapper.UserWebMapper;
import com.bookingapp.domain.service.UserService;
import com.bookingapp.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User profile and role management")
public class UserController {

    private final UserService userService;
    private final UserWebMapper userWebMapper;

    public UserController(
            UserService userService,
            UserWebMapper userWebMapper
    ) {
        this.userService = userService;
        this.userWebMapper = userWebMapper;
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role", security = @SecurityRequirement(name = "bearerAuth"))
    public UserProfileResponse updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        User updatedUser = userService.updateUserRole(
                userWebMapper.toUpdateUserRoleCommand(id, request)
        );
        return userWebMapper.toResponse(updatedUser);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public UserProfileResponse getCurrentUserProfile() {
        return userWebMapper.toResponse(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    @Operation(summary = "Replace current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public UserProfileResponse updateCurrentUserProfile(
            @Valid @RequestBody UpdateCurrentUserRequest request
    ) {
        /*
         * Password changes are intentionally excluded from profile endpoints to keep
         * this API focused on profile data and avoid mixing credential flows here.
         */
        User updatedUser = userService.updateCurrentUserProfile(
                userWebMapper.toUpdateProfileCommand(request)
        );
        return userWebMapper.toResponse(updatedUser);
    }

    @PatchMapping("/me")
    @Operation(summary = "Partially update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public UserProfileResponse patchCurrentUserProfile(
            @Valid @RequestBody PatchCurrentUserRequest request
    ) {
        User currentUser = userService.getCurrentUserProfile();
        User updatedUser = userService.updateCurrentUserProfile(
                userWebMapper.toPatchProfileCommand(request, currentUser)
        );
        return userWebMapper.toResponse(updatedUser);
    }
}
