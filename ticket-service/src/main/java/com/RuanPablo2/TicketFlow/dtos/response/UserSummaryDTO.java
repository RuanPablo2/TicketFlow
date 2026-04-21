package com.RuanPablo2.TicketFlow.dtos.response;

import com.RuanPablo2.TicketFlow.entity.enums.Role;

public record UserSummaryDTO(
        Long id,
        String name,
        String email,
        Role role
) {}