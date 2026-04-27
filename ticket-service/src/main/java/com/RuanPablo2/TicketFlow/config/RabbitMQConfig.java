package com.RuanPablo2.TicketFlow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TICKET_CREATED_QUEUE = "ticket.created.queue";
    public static final String TICKET_EXCHANGE = "ticket.exchange";
    public static final String TICKET_CREATED_ROUTING_KEY = "ticket.created.routingKey";

    public static final String PASSWORD_RESET_QUEUE = "password.reset.queue";
    public static final String PASSWORD_RESET_ROUTING_KEY = "auth.password.reset";

    @Bean
    public Queue ticketCreatedQueue() {
        return new Queue(TICKET_CREATED_QUEUE, true);
    }

    @Bean
    public DirectExchange ticketExchange() {
        return new DirectExchange(TICKET_EXCHANGE);
    }

    @Bean
    public Binding bindingTicketCreated(Queue ticketCreatedQueue, DirectExchange ticketExchange) {
        return BindingBuilder
                .bind(ticketCreatedQueue)
                .to(ticketExchange)
                .with(TICKET_CREATED_ROUTING_KEY);
    }

    @Bean
    public Queue passwordResetQueue() {
        return new Queue(PASSWORD_RESET_QUEUE, true);
    }

    @Bean
    public Binding passwordResetBinding(Queue passwordResetQueue, DirectExchange ticketExchange) {
        return BindingBuilder
                .bind(passwordResetQueue)
                .to(ticketExchange)
                .with(PASSWORD_RESET_ROUTING_KEY);
    }

    @Bean
    @SuppressWarnings("removal")
    public MessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(mapper);
    }
}