package com.RuanPablo2.notification_service.listener;

import com.RuanPablo2.notification_service.events.PasswordResetRequestedEvent;
import com.RuanPablo2.notification_service.events.TicketCreatedEvent;
import com.RuanPablo2.notification_service.service.EmailService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketNotificationListener {

    private final EmailService emailService;

    public TicketNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ticket.created.queue", durable = "true"),
            exchange = @Exchange(value = "ticket.exchange", type = "direct"),
            key = "ticket.created.routingKey"
    ))
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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "password.reset.queue", durable = "true"),
            exchange = @Exchange(value = "ticket.exchange", type = "direct"),
            key = "auth.password.reset"
    ))
    public void listenPasswordReset(PasswordResetRequestedEvent event) {
        System.out.println("🔐 [NEW NOTIFICATION] Dispatching Password Reset HTML email...");

        try {
            emailService.sendPasswordResetEmail(
                    event.email(),
                    event.name(),
                    event.token()
            );
            System.out.println("✅ Password Reset Email sent successfully to: " + event.email());

        } catch (Exception e) {
            System.err.println("❌ Error sending password reset email: " + e.getMessage());
        }
    }
}