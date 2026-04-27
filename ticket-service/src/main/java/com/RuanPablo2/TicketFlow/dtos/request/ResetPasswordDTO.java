package com.RuanPablo2.TicketFlow.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "The new password is required")
        @Size(min = 6, message = "The password must be at least 6 characters long")
        String newPassword
) {}