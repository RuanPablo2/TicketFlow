package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.dtos.request.TicketRequestDTO;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.entity.enums.TicketPriority;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.events.TicketCreatedEvent;
import com.RuanPablo2.TicketFlow.exceptions.BusinessRuleException;
import com.RuanPablo2.TicketFlow.exceptions.ErrorCode;
import com.RuanPablo2.TicketFlow.exceptions.ResourceNotFoundException;
import com.RuanPablo2.TicketFlow.exceptions.UnauthorizedAccessException;
import com.RuanPablo2.TicketFlow.publisher.TicketMessagePublisher;
import com.RuanPablo2.TicketFlow.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final TicketMessagePublisher messagePublisher;

    public TicketService(TicketRepository ticketRepository, UserService userService, TicketMessagePublisher messagePublisher) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.messagePublisher = messagePublisher;
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

        long openTickets = ticketRepository.countByClientIdAndStatusNot(clientId, TicketStatus.RESOLVED);
        if (openTickets >= 3) {
            throw new BusinessRuleException(
                    "You have reached the limit of open tickets. Please wait for support to resolve your current requests.",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketCreatedEvent event = new TicketCreatedEvent(
                savedTicket.getId(),
                savedTicket.getTitle(),
                client.getName()
        );
        messagePublisher.sendTicketCreatedEvent(event);

        return savedTicket;
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with ID: " + id,
                        ErrorCode.RESOURCE_NOT_FOUND));
    }

    public Ticket findByIdSecure(Long id, Long requesterId) {
        Ticket ticket = findById(id);
        User requester = userService.findById(requesterId);

        if (requester.getRole() == Role.CLIENT && !ticket.getClient().getId().equals(requester.getId())) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to access the data for this call.",
                    ErrorCode.UNAUTHORIZED_ACCESS
            );
        }

        return ticket;
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long supportId) {
        Ticket ticket = findById(ticketId);
        User supportUser = userService.findById(supportId);

        if (supportUser.getRole() == Role.CLIENT) {
            throw new BusinessRuleException(
                    "Only support staff can be assigned to resolve a support ticket.",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new BusinessRuleException(
                    "It is not possible to assign support to a ticket that has already been closed.",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        ticket.setAssignedSupport(supportUser);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus, Long requesterId) {
        Ticket ticket = findById(ticketId);
        User requester = userService.findById(requesterId);

        if (requester.getRole() == Role.CLIENT) {
            throw new UnauthorizedAccessException(
                    "Only support agents can change the status of a ticket.",
                    ErrorCode.UNAUTHORIZED_ACCESS
            );
        }

        if (ticket.getStatus() == TicketStatus.RESOLVED && newStatus != TicketStatus.OPEN) {
            throw new BusinessRuleException(
                    "The ticket is now closed. It can only be modified again if it is reopened (OPEN).",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        if (newStatus == TicketStatus.RESOLVED && ticket.getAssignedSupport() == null) {
            throw new BusinessRuleException(
                    "A ticket must be assigned to a support agent before it can be resolved.",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setClosedAt(LocalDateTime.now());
        } else {
            ticket.setClosedAt(null);
        }

        return ticketRepository.save(ticket);
    }

    public List<Ticket> findAllTicketsSecure(Long requesterId) {
        User requester = userService.findById(requesterId);

        if (requester.getRole() == Role.CLIENT) {
            return ticketRepository.findAllByClientId(requesterId);
        }

        return ticketRepository.findAll();
    }

    @Transactional
    public Ticket updateTicketPriority(Long ticketId, TicketPriority newPriority, Long requesterId) {
        Ticket ticket = findById(ticketId);
        User requester = userService.findById(requesterId);

        if (requester.getRole() == Role.CLIENT) {
            throw new UnauthorizedAccessException(
                    "Only support agents can perform ticket triage and change priority.",
                    ErrorCode.UNAUTHORIZED_ACCESS
            );
        }

        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new BusinessRuleException(
                    "It is not possible to change the priority of a closed ticket.",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        ticket.setPriority(newPriority);
        return ticketRepository.save(ticket);
    }
}