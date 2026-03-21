package com.bookingapp.application.port.in.user;

import com.bookingapp.application.dto.UpdateUserRoleCommand;
import com.bookingapp.domain.model.User;

public interface UpdateUserRoleUseCase {

    User updateUserRole(UpdateUserRoleCommand command);
}
