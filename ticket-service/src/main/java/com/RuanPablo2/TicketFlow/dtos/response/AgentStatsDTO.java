package com.RuanPablo2.TicketFlow.dtos.response;

public record AgentStatsDTO(
        String name,
        String role,
        long activeTickets,
        long resolvedThisWeek
) {}