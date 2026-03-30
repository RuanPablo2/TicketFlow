package com.RuanPablo2.TicketFlow.dtos.request;

import com.RuanPablo2.TicketFlow.entity.enums.TicketCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequestDTO(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Category is required")
        TicketCategory category
) {}