package com.RuanPablo2.TicketFlow.controller;

import com.RuanPablo2.TicketFlow.dtos.request.TicketRequestDTO;
import com.RuanPablo2.TicketFlow.dtos.response.AgentStatsDTO;
import com.RuanPablo2.TicketFlow.dtos.response.TicketResponseDTO;
import com.RuanPablo2.TicketFlow.dtos.response.TicketStatsDTO;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.TicketPriority;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.mappers.TicketMapper;
import com.RuanPablo2.TicketFlow.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoints para gerenciamento do ciclo de vida dos chamados de suporte")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    public TicketController(TicketService ticketService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
    }

    @PostMapping
    @Operation(summary = "Abre um novo Ticket", description = "Cria um chamado e dispara um evento assíncrono para notificação por e-mail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação (ex: campos em branco)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: Cliente atingiu o limite de tickets abertos")
    })
    public ResponseEntity<TicketResponseDTO> createTicket(
            @AuthenticationPrincipal User loggedUser,
            @Valid @RequestBody TicketRequestDTO requestDTO) {

        Ticket createdTicket = ticketService.createTicket(requestDTO, loggedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponseDTO(createdTicket));
    }

    @GetMapping
    @Operation(summary = "Lista os tickets (Paginado)", description = "Retorna uma lista paginada. Clientes veem apenas os próprios tickets; Suporte/Admin veem a fila global.")
    @ApiResponse(responseCode = "200", description = "Lista de tickets recuperada com sucesso")
    public ResponseEntity<Page<TicketResponseDTO>> getAllTickets(
            @AuthenticationPrincipal User loggedUser,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Ticket> ticketPage = ticketService.findAllTicketsSecure(loggedUser.getId(), pageable);
        Page<TicketResponseDTO> responsePage = ticketPage.map(ticketMapper::toResponseDTO);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca ticket por ID", description = "Retorna os detalhes de um ticket específico. Clientes só podem visualizar os próprios tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: O ticket pertence a outro cliente"),
            @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
    })
    public ResponseEntity<TicketResponseDTO> getTicketById(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        Ticket ticket = ticketService.findByIdSecure(id, loggedUser.getId());
        return ResponseEntity.ok(ticketMapper.toResponseDTO(ticket));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('SUPPORT')")
    @Operation(summary = "Atribui um ticket a um atendente", description = "O atendente logado assume a responsabilidade pelo ticket. O status muda para IN_PROGRESS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket atribuído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de regra de negócio (ex: tentar atribuir ticket fechado)"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a Suporte e Admin")
    })
    public ResponseEntity<TicketResponseDTO> assignTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        Ticket updatedTicket = ticketService.assignTicket(id, loggedUser.getId());
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }

    @GetMapping("/my-queue")
    @PreAuthorize("hasAnyRole('SUPPORT')")
    @Operation(summary = "Fila de trabalho do atendente", description = "Retorna de forma paginada os tickets que estão atribuídos ao atendente logado.")
    @ApiResponse(responseCode = "200", description = "Fila recuperada com sucesso")
    public ResponseEntity<Page<TicketResponseDTO>> getMyAssignedTickets(
            @AuthenticationPrincipal User loggedUser,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Ticket> ticketPage = ticketService.findTicketsBySupport(loggedUser.getId(), pageable);
        Page<TicketResponseDTO> responsePage = ticketPage.map(ticketMapper::toResponseDTO);

        return ResponseEntity.ok(responsePage);
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @Operation(summary = "Atualiza a prioridade", description = "Muda a prioridade do ticket (LOW, MEDIUM, HIGH, URGENT). Não é possível alterar a prioridade de tickets resolvidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prioridade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "O ticket já está fechado")
    })
    public ResponseEntity<TicketResponseDTO> updatePriority(
            @PathVariable Long id,
            @RequestParam TicketPriority priority) {

        Ticket updatedTicket = ticketService.updateTicketPriority(id, priority);
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @Operation(summary = "Atualiza o status", description = "Altera o status do ticket. Se for alterado para RESOLVED, a data de fechamento é registrada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra violada (ex: fechar ticket sem atendente)"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a Suporte e Admin")
    })
    public ResponseEntity<TicketResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {

        Ticket updatedTicket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }

    @PutMapping("/{id}/resume")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Retoma um ticket (Apenas Cliente)", description = "Permite que o cliente devolva o ticket para a fila do suporte após fornecer as informações solicitadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket retomado com sucesso"),
            @ApiResponse(responseCode = "400", description = "O ticket não está aguardando resposta do cliente"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: O ticket não pertence a este cliente")
    })
    public ResponseEntity<TicketResponseDTO> resumeTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        Ticket updatedTicket = ticketService.resumeTicket(id, loggedUser.getId());
        return ResponseEntity.ok(ticketMapper.toResponseDTO(updatedTicket));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @Operation(summary = "Estatísticas do Dashboard", description = "Retorna a contagem global de chamados agrupados por status para renderização de gráficos.")
    @ApiResponse(responseCode = "200", description = "Estatísticas recuperadas com sucesso")
    public ResponseEntity<TicketStatsDTO> getTicketStats() {

        TicketStatsDTO stats = ticketService.getTicketStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/agents")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Performance da Equipe", description = "Retorna métricas de resolução e carga de trabalho por atendente.")
    @ApiResponse(responseCode = "200", description = "Métricas recuperadas com sucesso")
    public ResponseEntity<List<AgentStatsDTO>> getAgentStats() {
        return ResponseEntity.ok(ticketService.getTopAgentsStats());
    }
}