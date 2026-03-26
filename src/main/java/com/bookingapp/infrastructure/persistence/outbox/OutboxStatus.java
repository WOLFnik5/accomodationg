package com.bookingapp.infrastructure.persistence.outbox;

public enum OutboxStatus {
    NEW,
    SENT,
    FAILED,
    DEAD
}
