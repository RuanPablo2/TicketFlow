package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.TicketRequestDTO;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @RequestHeader("User-Id") Long clientId,
            @Valid @RequestBody TicketRequestDTO requestDTO) {
        Ticket createdTicket = ticketService.createTicket(requestDTO, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.findAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Ticket> assignTicket(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long supportId) {
        Ticket updatedTicket = ticketService.assignTicket(id, supportId);
        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        Ticket updatedTicket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(updatedTicket);
    }
}