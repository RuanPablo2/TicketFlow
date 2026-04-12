package com.RuanPablo2.TicketFlow.repository;

import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findAllByClientId(Long clientId, Pageable pageable);

    Page<Ticket> findAllByAssignedSupportId(Long supportId, Pageable pageable);

    Long countByClientIdAndStatusNot(Long clientId, TicketStatus status);
}