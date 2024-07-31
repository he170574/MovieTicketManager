package com.movie_theater.repository;

import com.movie_theater.dto.BookingListDTO;
import com.movie_theater.dto.InvoiceItemDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.entity.InvoiceItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Integer>, JpaSpecificationExecutor<InvoiceItem> {

    @Query("SELECT new com.movie_theater.dto.InvoiceItemDTO(sch.movieName, sch.price,  sch.scheduleTime, CONCAT(sch.seatRow, '-', sch.seatColumn), sch.seatType,fh.foodName,fh.foodPrice,fh.foodSize) " +
            "FROM InvoiceItem ii " +
            "JOIN ii.invoice i " +
            "LEFT JOIN ii.scheduleSeatHis sch " +
            "LEFT JOIN ii.foodHis fh " +
            "WHERE i.account = :account AND i.invoiceId = :invoiceId")
    List<InvoiceItemDTO> findInvoiceItemByAccountAndInvoiceId(Account account, Integer invoiceId);

    @Query("SELECT new com.movie_theater.dto.InvoiceItemDTO(sch.movieName, sch.price, sch.scheduleTime, CONCAT(sch.seatRow, '-', sch.seatColumn), sch.seatType,i.bookingDate,ii.status,sch.cinemaRoomName) " +
            "FROM InvoiceItem ii " +
            "JOIN ii.invoice i " +
            "JOIN ii.scheduleSeatHis sch " +
            "LEFT JOIN i.promotion promo " +
            "WHERE i.account = :account AND sch.movieName LIKE %:movieName% AND ii.scheduleSeatHis IS NOT NULL  " +
            "ORDER BY i.bookingDate DESC ")
    Page<InvoiceItemDTO> findInvoiceItemByAccountAndMovieName(Account account, String movieName, Pageable pageable);

    @Query("SELECT II FROM InvoiceItem II " +
            "JOIN II.invoice I " +
            "WHERE CONCAT(II.invoiceItemId,'') LIKE :invoiceItemId " +
            "ORDER BY I.bookingDate DESC")
    Page<InvoiceItem> findByInvoiceItemIdContains(String invoiceItemId, Pageable pageable);
    @Transactional
    @Modifying
    @Query("UPDATE InvoiceItem ii SET ii.status = :status WHERE ii.invoiceItemId = :invoiceItemId")
    int updateInvoiceStatusByInvoiceItemId(@Param("invoiceItemId") Integer invoiceItemId, @Param("status") Boolean status);

    InvoiceItem getByInvoiceItemId(Integer invoiceItemId);

    Page<InvoiceItem> findByScheduleSeatHis_ScheduleSeatHisId(Integer scheduleSeatHisId, Pageable pageable);
}