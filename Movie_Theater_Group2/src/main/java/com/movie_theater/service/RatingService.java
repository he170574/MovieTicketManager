package com.movie_theater.service;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface RatingService {
    Rating createRating(Movie movie, Account account, Integer star, String content);

    Rating getRatingOfMovieByAccount(Movie movie, Account account);

    Map<Integer, Long> getRatingStatistic(Movie movie);

    Rating editRating(Movie movie, Account author, Integer star, String content);

    void deleteRating(Movie movie, Account author);

    Page<Rating> getRatingsByMovie(Movie movie, Integer pageNumber, Integer pageSize);
}
