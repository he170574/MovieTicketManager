package com.movie_theater.service.impl;

import com.movie_theater.dto.MovieScheduleRoomDTO;
import com.movie_theater.entity.*;
import com.movie_theater.entity.key.KeyMovieSchedule;
import com.movie_theater.repository.MovieRepository;
import com.movie_theater.repository.MovieScheduleRepository;
import com.movie_theater.repository.ScheduleSeatRepository;
import com.movie_theater.repository.SeatRepository;
import com.movie_theater.service.ScheduleSeatService;
import com.movie_theater.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleSeatServiceImpl implements ScheduleSeatService {

    private ScheduleSeatRepository scheduleSeatRepository;
    private MovieRepository movieRepository;
    private SeatRepository seatRepository;
    private MovieScheduleRepository movieScheduleRepository;

    @Autowired
    public ScheduleSeatServiceImpl(ScheduleSeatRepository scheduleSeatRepository,
                                   MovieRepository movieRepository,
                                   SeatRepository seatRepository,
                                   MovieScheduleRepository movieScheduleRepository) {
        this.scheduleSeatRepository = scheduleSeatRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
        this.movieScheduleRepository = movieScheduleRepository;
    }

    @Override
    public List<ScheduleSeat> getAll() {
        return scheduleSeatRepository.findAll();
    }

    @Override
    public Optional<ScheduleSeat> getOne(Integer id) {
        return scheduleSeatRepository.findById(id);
    }

    @Override
    public ScheduleSeat save(ScheduleSeat scheduleSeat) {
        return scheduleSeatRepository.save(scheduleSeat);
    }

    @Override
    public MovieSchedule getMovieSchedule(ScheduleSeat scheduleSeat) {
        return scheduleSeat.getMovieSchedule();
    }

    @Override
    public List<MovieScheduleRoomDTO> getAllMovieScheduleRoomDTO() {
        return scheduleSeatRepository.getAllMovieScheduleRoom();
    }

    @Override
    public List<MovieScheduleRoomDTO> getAllMovieScheduleRoomDTOByRoomIdAndDate(Integer roomId, LocalDate date) {
        return scheduleSeatRepository.getAllMovieScheduleRoomByCinemaRoomIdAndDate(roomId,date);
    }

    @Override
    public List<ScheduleSeat> getScheduleSeatByMovieSchedule(MovieSchedule movieSchedule) {
        return scheduleSeatRepository.getByMovieSchedule(movieSchedule);
    }

    @Override
    public List<MovieScheduleRoomDTO> getMovieScheduleByRoomIdAndScheduleId(Integer roomId, Integer scheduleId) {
        return scheduleSeatRepository.getAllMovieScheduleRoomByCinemaRoomIdScheduleId(roomId,scheduleId);
    }

    @Override
    public Integer getPriceBySeatTypeAndScheduleIdAndMovieId(int seatType,int movieId, int scheduleId) {
        return scheduleSeatRepository.getPriceBySeatTypeAndScheduleIdAndMovieId(seatType,movieId,scheduleId);
    }

    @Override
    public void deleteByMovieSchedule(MovieSchedule movieSchedule) {
        scheduleSeatRepository.deleteByMovieSchedule(movieSchedule);
    }

    @Override
    public List<ScheduleSeat> getByMovieSchedule(MovieSchedule movieSchedule) {
        return scheduleSeatRepository.getByMovieSchedule(movieSchedule);
    }

    @Override
    public int updateScheduleSeatStatus(boolean status, int scheduleSeatId) {
        return scheduleSeatRepository.updateScheduleSeatStatus(status, scheduleSeatId);
    }

}
