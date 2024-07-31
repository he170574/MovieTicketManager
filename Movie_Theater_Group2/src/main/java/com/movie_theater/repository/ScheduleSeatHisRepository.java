package com.movie_theater.repository;

import com.movie_theater.entity.ScheduleSeatHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleSeatHisRepository extends JpaRepository<ScheduleSeatHis,Integer> {
    List<ScheduleSeatHis> getByCinemaRoomIdAndMovieIdAndScheduleTime(int cinemaRoomId, int movieId, LocalDateTime scheduleTime);

    @Transactional
    @Query(" SELECT ssh FROM ScheduleSeatHis ssh " +
            "JOIN ssh.invoiceItems ii " +
            "JOIN ii.invoice i " +
            "WHERE i.invoiceId = :invoiceId")
    List<ScheduleSeatHis> getScheduleSeatHisByInvoiceId(int invoiceId);
}
