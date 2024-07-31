package com.movie_theater.service;

import com.movie_theater.entity.ScheduleSeatHis;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleSeatHisService {
    ScheduleSeatHis save(ScheduleSeatHis scheduleSeatHis);

    List<ScheduleSeatHis> getByCinemaRoomIdAndMovieIdAndScheduleTime(int cinemaRoomId, int movieId, LocalDateTime scheduleTime);
    List<ScheduleSeatHis> getScheduleSeatHisByInvoiceId(int invoiceId);
}
