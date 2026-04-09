package com.RuanPablo2.notification_service.listener;

import com.RuanPablo2.notification_service.events.TicketCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class TicketNotificationListener {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @RabbitListener(queues = "ticket.created.queue")
    public void listenTicketCreation(TicketCreatedEvent event) {
        System.out.println("📬 [NEW NOTIFICATION] Dispatching real email to the client...");

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(senderEmail);

            message.setTo(senderEmail);

            message.setSubject("Ticket Created: " + event.title());
            message.setText("Hello " + event.clientName() + ",\n\n" +
                    "Your ticket number #" + event.ticketId() + " has been successfully created in our system!\n" +
                    "Our team will get in touch with you shortly.\n\n" +
                    "Best regards,\nTicketFlow Team");

            mailSender.send(message);
            System.out.println("✅ Email sent successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
        }
    }
}