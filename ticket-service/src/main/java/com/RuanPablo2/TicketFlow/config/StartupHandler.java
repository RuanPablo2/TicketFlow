package com.RuanPablo2.TicketFlow.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupHandler {

    private final DemoSeeder demoSeeder;

    public StartupHandler(DemoSeeder demoSeeder) {
        this.demoSeeder = demoSeeder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        demoSeeder.seed();
    }
}