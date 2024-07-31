package com.movie_theater.service;

import com.movie_theater.dto.MovieDTO;
import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> getById(Integer id);

    List<Movie> getAll();

    Movie save(Movie movie);

    Movie update(Movie movie);

    Boolean delete(Integer id);

    Movie parseMovieDtoToMovie(MovieDTO movieDTO);

    public List<TypeMovie> getAllTypeMovieByMovieId(Integer id);

    List<MovieSchedule> getAllMovieScheduleByMovieId(Integer id);

    Integer deleteTypeMovieByMovie(Movie movie);

    MovieDTO parseMovieToMovieDto(Movie movie);

    Page<Movie> getAllMovieForMember(Pageable pageable);
    Page<Movie> findByKeywordAndNotDeleted(String keyword, Pageable pageable);

    List<Movie> getMoviePlayingByDate(LocalDate date);

    Page<Movie> getMovieByMovieName(String movieName, Pageable pageable);

    int updateDeleted(boolean deleted, int movieId);
}