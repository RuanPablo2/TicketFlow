package com.RuanPablo2.TicketFlow.repository;

import com.RuanPablo2.TicketFlow.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByTicketIdOrderByCreatedAtAsc(Long ticketId);
}