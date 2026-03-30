package com.RuanPablo2.TicketFlow.dtos.response;

import com.RuanPablo2.TicketFlow.entity.enums.TicketCategory;
import com.RuanPablo2.TicketFlow.entity.enums.TicketPriority;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponseDTO(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        TicketCategory category,
        UserSummaryDTO client,
        UserSummaryDTO assignedSupport,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {}