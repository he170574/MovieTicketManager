package com.movie_theater.service.impl;

import com.movie_theater.entity.ScheduleSeatHis;
import com.movie_theater.repository.ScheduleSeatHisRepository;
import com.movie_theater.service.ScheduleSeatHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleSeatHisServiceImpl implements ScheduleSeatHisService {

    ScheduleSeatHisRepository scheduleSeatHisRepository;

    @Autowired
    public ScheduleSeatHisServiceImpl(ScheduleSeatHisRepository scheduleSeatHisRepository) {
        this.scheduleSeatHisRepository = scheduleSeatHisRepository;
    }

    @Override
    public ScheduleSeatHis save(ScheduleSeatHis scheduleSeatHis) {
        return scheduleSeatHisRepository.save(scheduleSeatHis);
    }

    @Override
    public List<ScheduleSeatHis> getByCinemaRoomIdAndMovieIdAndScheduleTime(int cinemaRoomId, int movieId, LocalDateTime scheduleTime) {
        return scheduleSeatHisRepository.getByCinemaRoomIdAndMovieIdAndScheduleTime(cinemaRoomId,movieId,scheduleTime);
    }

    @Override
    public List<ScheduleSeatHis> getScheduleSeatHisByInvoiceId(int invoiceId) {
        return scheduleSeatHisRepository.getScheduleSeatHisByInvoiceId(invoiceId);
    }
}
