package com.RuanPablo2.TicketFlow.jobs;

import com.RuanPablo2.TicketFlow.config.DemoSeeder;
import com.RuanPablo2.TicketFlow.repository.MessageRepository;
import com.RuanPablo2.TicketFlow.repository.TicketRepository;
import com.RuanPablo2.TicketFlow.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoResetScheduler {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DemoSeeder demoSeeder;

    public DemoResetScheduler(MessageRepository messageRepository,
                              TicketRepository ticketRepository,
                              UserRepository userRepository,
                              DemoSeeder demoSeeder) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.demoSeeder = demoSeeder;
    }

    @Scheduled(cron = "0 0 0,12 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void resetDemoAccount() {
        System.out.println("🔄 [CRON JOB] Iniciando limpeza e restauração do cenário Demo...");

        messageRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        demoSeeder.seed();

        System.out.println("✅ [CRON JOB] Vitrine restaurada com sucesso! Atores e chamados originais reinseridos.");
    }
}