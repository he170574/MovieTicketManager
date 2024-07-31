package com.movie_theater.service.impl;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Invoice;
import com.movie_theater.repository.InvoiceRepository;
import com.movie_theater.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;



    @Override
    public Page<Invoice> findInvoicesByAccountAndDateRangeAndConditionalScores(Account account, LocalDateTime fromDate, LocalDateTime toDate, boolean viewScoreAdding, boolean viewScoreUsing, Pageable pageable) {
        return invoiceRepository.findInvoicesByAccountAndDateRangeAndConditionalScores(account,fromDate,toDate,viewScoreAdding,viewScoreUsing,pageable);
    }

    @Override
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getInvoicesByInvoiceId(int invoiceId) {
        return invoiceRepository.getInvoicesByInvoiceId(invoiceId);
    }

    @Override
    public int updateStatus(String status, int invoiceId) {
        return invoiceRepository.updateStatus(status, invoiceId);
    }

    @Override
    public Map<LocalDate, Long> findTicketCountPerDay(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateList = startDate.datesUntil(endDate.plusDays(1)).toList();

        // fetch data from database
        List<Object[]> data = invoiceRepository.findTicketCountPerDay(startDate.atStartOfDay(),LocalDateTime.of(endDate, LocalTime.MAX));

        Map<LocalDate, Long> result = new HashMap<>();
        for (Object[] row : data) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = (Long) row[1];
            result.put(date, count);
        }
        for(LocalDate date : dateList) {
            result.putIfAbsent(date, 0L);
        }
        return result;
    }
}
