package com.RuanPablo2.TicketFlow.dtos.request;
import jakarta.validation.constraints.NotBlank;

public record MessageRequestDTO(
        @NotBlank(message = "Message content cannot be empty")
        String content,

        boolean internalNote
) {}