package com.movie_theater.service;

import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.MovieSchedule;
import com.movie_theater.entity.Schedule;
import com.movie_theater.entity.key.KeyMovieSchedule;

import java.util.List;
import java.util.Optional;

public interface MovieScheduleService {
    Optional<MovieSchedule> getById(KeyMovieSchedule keyMovieSchedule);

    List<MovieSchedule> getAll();

    MovieSchedule save(MovieSchedule movieSchedule);

    Boolean delete(KeyMovieSchedule keyMovieSchedule);

    List<MovieSchedule> getByMovie(Movie movie);

    MovieSchedule getByMovieAndSchedule(Movie movie, Schedule schedule);

    List<MovieSchedule> getByCinemaRoom(CinemaRoom cinemaRoom);

    void delete(MovieSchedule movieSchedule);

    List<MovieSchedule> getMovieScheduleByMovieAndDeletedIsFalse(Movie movie);

}
