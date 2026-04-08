package com.RuanPablo2.notification_service.listener;

import com.RuanPablo2.notification_service.events.TicketCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketNotificationListener {

    @RabbitListener(queues = "ticket.created.queue")
    public void listenTicketCreation(TicketCreatedEvent event) {
        System.out.println("=====================================================");
        System.out.println("📬 [NEW NOTIFICATION] Preparing to send email...");
        System.out.println("👤 Client: " + event.clientName());
        System.out.println("🎫 Ticket ID: " + event.ticketId());
        System.out.println("📝 Subject: " + event.title());
        System.out.println("=====================================================");
    }
}