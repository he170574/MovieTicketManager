package com.movie_theater.repository;

import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.MovieSchedule;
import com.movie_theater.entity.Schedule;
import com.movie_theater.entity.key.KeyMovieSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, KeyMovieSchedule> {
    List<MovieSchedule> getMovieScheduleByMovie(Movie movie);

    MovieSchedule getByMovieAndAndSchedule(Movie movie, Schedule schedule);

    @Modifying
    @Query(" SELECT ms FROM MovieSchedule ms " +
            "JOIN ms.scheduleSeats ss " +
            "JOIN ss.seat seat " +
            "JOIN seat.cinemaRoom cr " +
            "WHERE cr = :cinemaRoom")
    List<MovieSchedule> getByCinemaRoom(CinemaRoom cinemaRoom);

    List<MovieSchedule> getMovieScheduleByMovieAndDeletedIsFalse(Movie movie);
}
