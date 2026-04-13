package com.RuanPablo2.TicketFlow.events;

public record TicketCreatedEvent(
        Long ticketId,
        String title,
        String userName,
        String email
) {}