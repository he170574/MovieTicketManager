package com.movie_theater.service;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface InvoiceService {
    Page<Invoice> findInvoicesByAccountAndDateRangeAndConditionalScores(Account account, LocalDateTime fromDate, LocalDateTime toDate, boolean viewScoreAdding, boolean viewScoreUsing, Pageable pageable);

    Invoice save(Invoice invoice);

    Invoice getInvoicesByInvoiceId(int invoiceId);

    int updateStatus(String status, int invoiceId);

    Map<LocalDate, Long> findTicketCountPerDay(LocalDate startDate, LocalDate endDate);

}