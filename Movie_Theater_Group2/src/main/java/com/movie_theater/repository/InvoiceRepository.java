package com.movie_theater.repository;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    @Query("SELECT i FROM Invoice i WHERE i.account = :account AND i.bookingDate >= :fromDate AND i.bookingDate <= :toDate " +
            "AND ((:viewScoreAdding = true AND i.addScore > 0) OR (:viewScoreUsing = true AND i.useScore > 0))" +
            "AND i.deleted = false")
    Page<Invoice> findInvoicesByAccountAndDateRangeAndConditionalScores(Account account, LocalDateTime fromDate, LocalDateTime toDate, boolean viewScoreAdding, boolean viewScoreUsing, Pageable pageable);

    Invoice getInvoicesByInvoiceId(int invoiceId);
    @Modifying
    @Transactional
    @Query("UPDATE Invoice i SET i.status = :status where i.invoiceId = :invoiceId")
    int updateStatus(@Param("status") String status, @Param("invoiceId") int invoiceId);

    @Query("SELECT CAST(t.bookingDate as DATE) AS bookingDate, COUNT(t) AS count FROM Invoice t WHERE t.bookingDate BETWEEN :startDate AND :endDate GROUP BY bookingDate")
    List<Object[]> findTicketCountPerDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
