package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.dtos.request.TicketRequestDTO;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.TicketPriority;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.exceptions.ErrorCode;
import com.RuanPablo2.TicketFlow.exceptions.ResourceNotFoundException;
import com.RuanPablo2.TicketFlow.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    public TicketService(TicketRepository ticketRepository, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
    }

    @Transactional
    public Ticket createTicket(TicketRequestDTO requestDTO, Long clientId) {
        User client = userService.findById(clientId);

        Ticket ticket = new Ticket();
        ticket.setTitle(requestDTO.title());
        ticket.setDescription(requestDTO.description());
        ticket.setCategory(requestDTO.category());
        ticket.setPriority(TicketPriority.LOW);

        ticket.setStatus(TicketStatus.OPEN);
        ticket.setClient(client);

        return ticketRepository.save(ticket);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with ID: " + id,
                        ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long supportId) {
        Ticket ticket = findById(ticketId);
        User supportUser = userService.findById(supportId);

        ticket.setAssignedSupport(supportUser);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = findById(ticketId);
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setClosedAt(LocalDateTime.now());
        } else {
            ticket.setClosedAt(null);
        }

        return ticketRepository.save(ticket);
    }

    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findTicketsByClient(Long clientId) {
        return ticketRepository.findAllByClientId(clientId);
    }
}