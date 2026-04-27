package com.RuanPablo2.notification_service.events;

public record PasswordResetRequestedEvent(
        String email,
        String name,
        String token
) {}