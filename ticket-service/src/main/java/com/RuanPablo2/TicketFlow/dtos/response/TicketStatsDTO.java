package com.RuanPablo2.TicketFlow.dtos.response;

public record TicketStatsDTO(
        long open,
        long inProgress,
        long waiting,
        long resolved
) {}