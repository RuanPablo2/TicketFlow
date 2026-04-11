package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.TicketRequestDTO;
import com.RuanPablo2.TicketFlow.dtos.response.TicketResponseDTO;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.TicketPriority;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.mappers.TicketMapper;
import com.RuanPablo2.TicketFlow.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    public TicketController(TicketService ticketService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(
            @AuthenticationPrincipal User loggedUser,
            @Valid @RequestBody TicketRequestDTO requestDTO) {

        Ticket createdTicket = ticketService.createTicket(requestDTO, loggedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponseDTO(createdTicket));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets(
            @AuthenticationPrincipal User loggedUser) {

        List<TicketResponseDTO> tickets = ticketService.findAllTicketsSecure(loggedUser.getId())
                .stream()
                .map(ticketMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        Ticket ticket = ticketService.findByIdSecure(id, loggedUser.getId());
        return ResponseEntity.ok(ticketMapper.toResponseDTO(ticket));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<TicketResponseDTO> assignTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        Ticket updatedTicket = ticketService.assignTicket(id, loggedUser.getId());
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<TicketResponseDTO> updatePriority(
            @PathVariable Long id,
            @RequestParam TicketPriority priority) {

        Ticket updatedTicket = ticketService.updateTicketPriority(id, priority);
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<TicketResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {

        Ticket updatedTicket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }
}