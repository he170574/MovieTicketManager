package com.movie_theater.repository;

import com.movie_theater.dto.BookingListDTO;
import com.movie_theater.entity.ScheduleSeatHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingListRepository extends JpaRepository<ScheduleSeatHis, Integer> {

    @Query("SELECT new com.movie_theater.dto.BookingListDTO(" +
            "i.invoiceId, " +
            "acc.fullName, " +
            "acc.phoneNumber, " +
            "ss.movieName, " +
            "ss.scheduleTime, " + // Ensure this is a LocalDateTime in the query
            "concat( ss.seatRow , " + // Ensure this is an integer in the query
            "ss.seatColumn))" + // This needs to be selected in the JPQL query
            "FROM Invoice i " +
            "JOIN i.account acc " +
            "JOIN i.invoiceItem ii " +
            "JOIN ii.scheduleSeatHis ss")
    List<BookingListDTO> findActiveBookings();

    @Query("SELECT new com.movie_theater.dto.BookingListDTO(" +
            "i.invoiceId, " +
            "acc.fullName, " +
            "acc.phoneNumber, " +
            "ss.movieName, " +
            "ss.scheduleTime, " +
            "concat( ss.seatRow , " + // Ensure this is an integer in the query
            "ss.seatColumn))" +
            "FROM Invoice i " +
            "JOIN i.account acc " +
            "JOIN i.invoiceItem ii " +
            "JOIN ii.scheduleSeatHis ss " +
            "WHERE i.invoiceId = ?1")
    List<BookingListDTO> findByInvoiceID(String idSearch);


}
