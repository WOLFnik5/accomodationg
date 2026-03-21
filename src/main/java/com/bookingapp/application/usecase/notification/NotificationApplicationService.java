package com.bookingapp.application.usecase.notification;

import com.bookingapp.application.port.in.notification.SendNotificationUseCase;
import com.bookingapp.application.port.out.integration.NotificationPort;
import com.bookingapp.domain.exception.BusinessValidationException;
import org.springframework.stereotype.Service;

@Service
public class NotificationApplicationService implements SendNotificationUseCase {

    private final NotificationPort notificationPort;

    public NotificationApplicationService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @Override
    public void sendMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new BusinessValidationException("Notification message must not be blank");
        }
        notificationPort.sendMessage(message);
    }
}
