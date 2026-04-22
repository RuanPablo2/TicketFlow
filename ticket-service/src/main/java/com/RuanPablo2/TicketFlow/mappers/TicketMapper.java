package com.RuanPablo2.TicketFlow.mappers;

import com.RuanPablo2.TicketFlow.dtos.response.TicketResponseDTO;
import com.RuanPablo2.TicketFlow.dtos.response.UserSummaryDTO;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponseDTO toResponseDTO(Ticket ticket) {

        UserSummaryDTO clientDTO = new UserSummaryDTO(
                ticket.getClient().getId(),
                ticket.getClient().getName(),
                ticket.getClient().getEmail(),
                ticket.getClient().getRole()
        );

        UserSummaryDTO supportDTO = null;
        if (ticket.getAssignedSupport() != null) {
            supportDTO = new UserSummaryDTO(
                    ticket.getAssignedSupport().getId(),
                    ticket.getAssignedSupport().getName(),
                    ticket.getAssignedSupport().getEmail(),
                    ticket.getAssignedSupport().getRole()
            );
        }

        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCategory(),
                clientDTO,
                supportDTO,
                ticket.getCreatedAt(),
                ticket.getClosedAt()
        );
    }
}