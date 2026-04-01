package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.dtos.request.MessageRequestDTO;
import com.RuanPablo2.TicketFlow.entity.Message;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.exceptions.BusinessRuleException;
import com.RuanPablo2.TicketFlow.exceptions.ErrorCode;
import com.RuanPablo2.TicketFlow.exceptions.UnauthorizedAccessException;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final TicketService ticketService;
    private final UserService userService;

    public MessageService(MessageRepository messageRepository, TicketService ticketService, UserService userService) {
        this.messageRepository = messageRepository;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @Transactional
    public Message addMessage(Long ticketId, Long senderId, MessageRequestDTO requestDTO) {
        Ticket ticket = ticketService.findById(ticketId);
        User sender = userService.findById(senderId);

        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new BusinessRuleException(
                    "It is not possible to add messages to a ticket that has already been closed.",
                    ErrorCode.BUSINESS_RULE_VIOLATION
            );
        }

        if (sender.getRole() == Role.CLIENT && !ticket.getClient().getId().equals(sender.getId())) {
            throw new UnauthorizedAccessException(
                    "You are not permitted to interact with another customer's call.",
                    ErrorCode.UNAUTHORIZED_ACCESS
            );
        }

        Message message = new Message();
        message.setContent(requestDTO.content());
        message.setInternalNote(sender.getRole() == Role.CLIENT ? false : requestDTO.internalNote());
        message.setTicket(ticket);
        message.setSender(sender);

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByTicket(Long ticketId, Long requesterId) {
        Ticket ticket = ticketService.findById(ticketId);
        User requester = userService.findById(requesterId);

        if (requester.getRole() == Role.CLIENT && !ticket.getClient().getId().equals(requester.getId())) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to view the messages in this ticket.",
                    ErrorCode.UNAUTHORIZED_ACCESS
            );
        }

        List<Message> allMessages = messageRepository.findAllByTicketIdOrderByCreatedAtAsc(ticketId);

        if (requester.getRole() == Role.CLIENT) {
            return allMessages.stream()
                    .filter(message -> !message.isInternalNote())
                    .collect(Collectors.toList());
        }

        return allMessages;
    }
}