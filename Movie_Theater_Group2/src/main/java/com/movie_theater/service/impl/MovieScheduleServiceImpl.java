package com.movie_theater.service.impl;

import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.MovieSchedule;
import com.movie_theater.entity.Schedule;
import com.movie_theater.entity.key.KeyMovieSchedule;
import com.movie_theater.repository.MovieScheduleRepository;
import com.movie_theater.service.MovieScheduleService;
import com.movie_theater.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieScheduleServiceImpl implements MovieScheduleService {
    private MovieScheduleRepository movieScheduleRepository;

    @Autowired
    public MovieScheduleServiceImpl(MovieScheduleRepository movieScheduleRepository) {
        this.movieScheduleRepository = movieScheduleRepository;
    }

    @Override
    public Optional<MovieSchedule> getById(KeyMovieSchedule keyMovieSchedule) {
        return movieScheduleRepository.findById(keyMovieSchedule);
    }

    @Override
    public List<MovieSchedule> getAll() {
        return movieScheduleRepository.findAll();
    }

    @Override
    public MovieSchedule save(MovieSchedule movieSchedule) {
        return movieScheduleRepository.save(movieSchedule);
    }

    @Override
    public Boolean delete(KeyMovieSchedule keyMovieSchedule) {
        Optional<MovieSchedule> optionalMovieSchedule = getById(keyMovieSchedule);
        if (optionalMovieSchedule.isPresent()) {
            MovieSchedule movie = optionalMovieSchedule.get();
            movie.setDeleted(Boolean.TRUE);
            return true;
        }
        return false;
    }

    @Override
    public List<MovieSchedule> getByMovie(Movie movie) {
        return movieScheduleRepository.getMovieScheduleByMovie(movie);
    }

    @Override
    public MovieSchedule getByMovieAndSchedule(Movie movie, Schedule schedule) {
        return movieScheduleRepository.getByMovieAndAndSchedule(movie,schedule);
    }

    @Override
    public List<MovieSchedule> getByCinemaRoom(CinemaRoom cinemaRoom) {
        return movieScheduleRepository.getByCinemaRoom(cinemaRoom);
    }

    @Override
    public void delete(MovieSchedule movieSchedule) {
         movieScheduleRepository.delete(movieSchedule);
    }

    @Override
    public List<MovieSchedule> getMovieScheduleByMovieAndDeletedIsFalse(Movie movie) {
        return movieScheduleRepository.getMovieScheduleByMovieAndDeletedIsFalse(movie);
    }
}
