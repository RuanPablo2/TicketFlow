package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.MessageRequestDTO;
import com.RuanPablo2.TicketFlow.dtos.response.MessageResponseDTO;
import com.RuanPablo2.TicketFlow.entity.Message;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.mappers.MessageMapper;
import com.RuanPablo2.TicketFlow.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets/{ticketId}/messages")
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public MessageController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> addMessage(
            @PathVariable Long ticketId,
            @RequestHeader("User-Id") Long senderId,
            @Valid @RequestBody MessageRequestDTO requestDTO) {

        Message createdMessage = messageService.addMessage(ticketId, senderId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageMapper.toResponseDTO(createdMessage));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @PathVariable Long ticketId,
            @RequestHeader("Requester-Role") Role requesterRole) {

        List<MessageResponseDTO> messages = messageService.getMessagesByTicket(ticketId, requesterRole)
                .stream()
                .map(messageMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }
}