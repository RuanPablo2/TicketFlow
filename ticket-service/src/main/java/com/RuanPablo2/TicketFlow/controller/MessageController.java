package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.MessageRequestDTO;
import com.RuanPablo2.TicketFlow.dtos.response.MessageResponseDTO;
import com.RuanPablo2.TicketFlow.entity.Message;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.mappers.MessageMapper;
import com.RuanPablo2.TicketFlow.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets/{ticketId}/messages")
@Tag(name = "Messages", description = "Endpoints para o chat e histórico de interações dentro de um ticket específico")
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public MessageController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @PostMapping
    @Operation(summary = "Adiciona uma nova mensagem", description = "Envia uma mensagem para o ticket especificado. Clientes só podem comentar nos próprios tickets; Suporte/Admin podem comentar em qualquer ticket atribuído.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mensagem adicionada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação (ex: mensagem vazia) ou ticket já está fechado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Sem permissão para interagir com este ticket"),
            @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
    })
    public ResponseEntity<MessageResponseDTO> addMessage(
            @Parameter(description = "ID do ticket onde a mensagem será adicionada", example = "1")
            @PathVariable Long ticketId,
            @AuthenticationPrincipal User loggedUser,
            @Valid @RequestBody MessageRequestDTO requestDTO) {

        Message createdMessage = messageService.addMessage(ticketId, loggedUser.getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageMapper.toResponseDTO(createdMessage));
    }

    @GetMapping
    @Operation(summary = "Lista o histórico de mensagens", description = "Recupera todas as interações e mensagens de um ticket. O acesso é restrito ao dono do ticket e aos agentes de suporte.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de mensagens recuperado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: O ticket pertence a outro usuário"),
            @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
    })
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @Parameter(description = "ID do ticket para buscar o histórico", example = "1")
            @PathVariable Long ticketId,
            @AuthenticationPrincipal User loggedUser) {

        List<MessageResponseDTO> messages = messageService.getMessagesByTicket(ticketId, loggedUser.getId())
                .stream()
                .map(messageMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }
}