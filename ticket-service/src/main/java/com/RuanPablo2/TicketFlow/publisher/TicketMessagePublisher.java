package com.RuanPablo2.TicketFlow.publisher;

import com.RuanPablo2.TicketFlow.config.RabbitMQConfig;
import com.RuanPablo2.TicketFlow.events.TicketCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TicketMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public TicketMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTicketCreatedEvent(TicketCreatedEvent event) {
        System.out.println("Sending message to RabbitMQ: " + event.title());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TICKET_EXCHANGE,
                RabbitMQConfig.TICKET_CREATED_ROUTING_KEY,
                event
        );
    }
}