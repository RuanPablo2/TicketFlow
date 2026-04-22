package com.RuanPablo2.TicketFlow.mappers;

import com.RuanPablo2.TicketFlow.dtos.response.MessageResponseDTO;
import com.RuanPablo2.TicketFlow.dtos.response.UserSummaryDTO;
import com.RuanPablo2.TicketFlow.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponseDTO(Message message) {

        UserSummaryDTO senderDTO = new UserSummaryDTO(
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSender().getEmail(),
                message.getSender().getRole()
        );

        return new MessageResponseDTO(
                message.getId(),
                message.getContent(),
                message.isInternalNote(),
                senderDTO,
                message.getCreatedAt()
        );
    }
}