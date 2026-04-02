package com.RuanPablo2.TicketFlow.repository;

import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByClientId(Long clientId);

    List<Ticket> findAllByAssignedSupportId(Long supportId);

    Long countByClientIdAndStatusNot(Long clientId, TicketStatus status);
}