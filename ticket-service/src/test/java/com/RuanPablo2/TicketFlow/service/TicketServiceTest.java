package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.exceptions.BusinessRuleException;
import com.RuanPablo2.TicketFlow.exceptions.UnauthorizedAccessException;
import com.RuanPablo2.TicketFlow.repository.TicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TicketService ticketService;

    @Test
    @DisplayName("An UnauthorizedAccessException occurs when a client attempts to access another client's ticket")
    void findByIdSecure_ShouldThrowException_WhenClientAccessesAnotherClientTicket() {

        Long ticketId = 1L;
        Long hackerClientId = 99L;
        Long ownerClientId = 5L;

        User ticketOwner = new User();
        ticketOwner.setId(ownerClientId);
        ticketOwner.setRole(Role.CLIENT);

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setClient(ticketOwner);

        User hackerClient = new User();
        hackerClient.setId(hackerClientId);
        hackerClient.setRole(Role.CLIENT);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userService.findById(hackerClientId)).thenReturn(hackerClient);

        assertThrows(UnauthorizedAccessException.class, () -> {
            ticketService.findByIdSecure(ticketId, hackerClientId);
        });
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when a client tries to change the ticket status")
    void updateTicketStatus_ShouldThrowException_WhenRequesterIsClient() {
        Long ticketId = 1L;
        Long clientId = 5L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        User clientUser = new User();
        clientUser.setId(clientId);
        clientUser.setRole(Role.CLIENT);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userService.findById(clientId)).thenReturn(clientUser);

        assertThrows(UnauthorizedAccessException.class, () -> {
            ticketService.updateTicketStatus(ticketId, TicketStatus.RESOLVED, clientId);
        });
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when trying to resolve a ticket without assigned support")
    void updateTicketStatus_ShouldThrowException_WhenResolvingUnassignedTicket() {
        Long ticketId = 1L;
        Long supportId = 10L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedSupport(null);

        User supportUser = new User();
        supportUser.setId(supportId);
        supportUser.setRole(Role.SUPPORT);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userService.findById(supportId)).thenReturn(supportUser);

        assertThrows(BusinessRuleException.class, () -> {
            ticketService.updateTicketStatus(ticketId, TicketStatus.RESOLVED, supportId);
        });
    }

    @Test
    @DisplayName("Should successfully assign support to the ticket and change status to IN_PROGRESS")
    void assignTicket_ShouldAssignSupportAndChangeStatus_WhenValid() {
        Long ticketId = 1L;
        Long supportId = 10L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(TicketStatus.OPEN);

        User supportUser = new User();
        supportUser.setId(supportId);
        supportUser.setRole(Role.SUPPORT);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userService.findById(supportId)).thenReturn(supportUser);

        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket updatedTicket = ticketService.assignTicket(ticketId, supportId);

        org.junit.jupiter.api.Assertions.assertEquals(TicketStatus.IN_PROGRESS, updatedTicket.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(supportUser, updatedTicket.getAssignedSupport());
    }
}