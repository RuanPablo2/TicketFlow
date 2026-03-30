package com.RuanPablo2.TicketFlow.dtos.response;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        Long id,
        String content,
        boolean internalNote,
        UserSummaryDTO sender,
        LocalDateTime createdAt
) {}