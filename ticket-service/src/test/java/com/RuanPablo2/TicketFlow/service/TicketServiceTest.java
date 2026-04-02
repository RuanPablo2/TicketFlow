package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
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
}