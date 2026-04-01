package com.RuanPablo2.TicketFlow.dtos.request;

import com.RuanPablo2.TicketFlow.entity.enums.Role;

public record RegisterDTO(String name, String email, String password, Role role) {}