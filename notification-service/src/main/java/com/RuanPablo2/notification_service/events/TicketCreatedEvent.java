package com.RuanPablo2.notification_service.events;

public record TicketCreatedEvent(
        Long ticketId,
        String title,
        String userName,
        String email
) {}