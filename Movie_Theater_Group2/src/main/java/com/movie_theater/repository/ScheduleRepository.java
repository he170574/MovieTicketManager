package com.movie_theater.repository;

import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Optional<Schedule> getSchedulesByScheduleTime(LocalDateTime scheduleTime);

    @Modifying
    @Query(" SELECT s FROM Schedule s " +
            "JOIN s.movieSchedules ms " +
            "JOIN ms.scheduleSeats ss " +
            "JOIN ss.seat seat " +
            "JOIN seat.cinemaRoom cr " +
            "WHERE cr = :cinemaRoom")
    List<Schedule> getByCinemaRoom(CinemaRoom cinemaRoom);
    @Modifying
    @Query(" SELECT s FROM Schedule s " +
            "JOIN s.movieSchedules ms " +
            "JOIN ms.movie m " +
            "WHERE m.movieId = :movieId")
    List<Schedule> getScheduleByMovieId(int movieId);


}
