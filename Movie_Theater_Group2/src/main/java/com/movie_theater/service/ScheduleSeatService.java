package com.movie_theater.service;

import com.movie_theater.dto.MovieScheduleRoomDTO;
import com.movie_theater.entity.*;
import com.movie_theater.entity.key.KeyMovieSchedule;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleSeatService {

    List<ScheduleSeat> getAll();

    Optional<ScheduleSeat> getOne(Integer id);

    ScheduleSeat save(ScheduleSeat scheduleSeat);

    MovieSchedule getMovieSchedule(ScheduleSeat scheduleSeat);

    List<MovieScheduleRoomDTO> getAllMovieScheduleRoomDTO();

    List<MovieScheduleRoomDTO> getAllMovieScheduleRoomDTOByRoomIdAndDate(Integer roomId, LocalDate day);

    List<MovieScheduleRoomDTO> getMovieScheduleByRoomIdAndScheduleId(Integer roomId,Integer scheduleId);

    List<ScheduleSeat> getScheduleSeatByMovieSchedule(MovieSchedule movieSchedule);

    Integer getPriceBySeatTypeAndScheduleIdAndMovieId(int seatType,int movieId,int scheduleId);

    void deleteByMovieSchedule(MovieSchedule movieSchedule);

    List<ScheduleSeat> getByMovieSchedule(MovieSchedule movieSchedule);

    int updateScheduleSeatStatus(boolean status, int scheduleSeatId);
}
