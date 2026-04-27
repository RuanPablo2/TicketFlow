package com.RuanPablo2.TicketFlow.events;

public record PasswordResetRequestedEvent(
        String email,
        String name,
        String token
) {}