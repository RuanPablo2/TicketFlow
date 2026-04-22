package com.RuanPablo2.TicketFlow.repository;

import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findAllByClientId(Long clientId, Pageable pageable);

    Page<Ticket> findAllByAssignedSupportId(Long supportId, Pageable pageable);

    Long countByClientIdAndStatusNot(Long clientId, TicketStatus status);

    Long countByStatus(TicketStatus status);

    @Query("SELECT t.assignedSupport.name, " +
            "SUM(CASE WHEN t.status = :inProgress THEN 1L ELSE 0L END), " +
            "SUM(CASE WHEN t.status = :resolved THEN 1L ELSE 0L END) " +
            "FROM Ticket t WHERE t.assignedSupport IS NOT NULL " +
            "GROUP BY t.assignedSupport.id, t.assignedSupport.name")
    List<Object[]> findAgentPerformanceMetrics(
            @Param("inProgress") TicketStatus inProgress,
            @Param("resolved") TicketStatus resolved
    );
}