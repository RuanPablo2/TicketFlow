package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.MessageRequestDTO;
import com.RuanPablo2.TicketFlow.entity.Message;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Message> addMessage(
            @PathVariable Long ticketId,
            @RequestHeader("User-Id") Long senderId,
            @Valid @RequestBody MessageRequestDTO requestDTO) {
        Message createdMessage = messageService.addMessage(ticketId, senderId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Long ticketId,
            @RequestHeader("Requester-Role") Role requesterRole) {
        List<Message> messages = messageService.getMessagesByTicket(ticketId, requesterRole);
        return ResponseEntity.ok(messages);
    }
}