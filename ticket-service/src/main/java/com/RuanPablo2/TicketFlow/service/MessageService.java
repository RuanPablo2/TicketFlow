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

        Message message = new Message();
        message.setContent(requestDTO.content());
        message.setInternalNote(requestDTO.internalNote());
        message.setTicket(ticket);
        message.setSender(sender);

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByTicket(Long ticketId, Role requesterRole) {
        List<Message> allMessages = messageRepository.findAllByTicketIdOrderByCreatedAtAsc(ticketId);

        if (requesterRole == Role.CLIENT) {
            return allMessages.stream()
                    .filter(message -> !message.isInternalNote())
                    .collect(Collectors.toList());
        }

        return allMessages;
    }
}