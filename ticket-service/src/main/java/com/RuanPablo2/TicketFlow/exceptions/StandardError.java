package com.RuanPablo2.TicketFlow.exceptions;

import java.time.LocalDateTime;

public record StandardError(
        LocalDateTime timestamp,
        Integer status,
        String errorCode,
        String error,
        String message,
        String path
) {}