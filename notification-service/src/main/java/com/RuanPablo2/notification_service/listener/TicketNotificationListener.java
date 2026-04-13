package com.RuanPablo2.notification_service.listener;

import com.RuanPablo2.notification_service.events.TicketCreatedEvent;
import com.RuanPablo2.notification_service.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketNotificationListener {

    private final EmailService emailService;

    public TicketNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "ticket.created.queue")
    public void listenTicketCreation(TicketCreatedEvent event) {
        System.out.println("📬 [NEW NOTIFICATION] Dispatching HTML email to the client...");

        try {
            emailService.sendHtmlEmail(
                    "ruanpablo2.dev@gmail.com",
                    "Ticket confirmation #" + event.ticketId(),
                    event.userName(),
                    event.title(),
                    event.ticketId()
            );
            System.out.println("✅ HTML Email sent successfully to: " + event.email());

        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
        }
    }
}