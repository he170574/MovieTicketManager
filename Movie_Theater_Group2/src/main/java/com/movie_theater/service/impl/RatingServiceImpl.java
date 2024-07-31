package com.movie_theater.service.impl;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.Rating;
import com.movie_theater.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
public class RatingServiceImpl implements RatingService {

    private Map<Integer, Map<Integer, Rating>> data;

    public RatingServiceImpl() {
        data = new HashMap<>();

        Map<Integer, Rating> map = new HashMap<>();
        map.put(1, Rating.builder()
                .star(3)
                .content("Nah")
                .author(Account.builder().accountId(1).username("Admin").build())
                .movie(Movie.builder().movieId(1).build())
                .build());
        data.putIfAbsent(1, map);
    }

    public Map<Integer, Rating> getMovieRatingMap(Movie movie) {
        if (!data.containsKey(movie.getMovieId())) {
            data.put(movie.getMovieId(), new HashMap<>());
        }
        return data.get(movie.getMovieId());
    }

    @Override
    public Rating createRating(Movie movie, Account author, Integer star, String content) {
        Map<Integer, Rating> movieRatings = getMovieRatingMap(movie);

        if (content == null || content.isEmpty()) throw new RuntimeException("Comment cannot empty!");
        if (star < 1 || star > 5) throw new RuntimeException("Star must in range 1 - 5!");

        Rating rating = Rating.builder().author(author).movie(movie).star(star).content(content).build();
        if (movieRatings.containsKey(author.getAccountId())) throw new RuntimeException("Already rated!");

        movieRatings.put(author.getAccountId(), rating);

        return rating;
    }

    @Override
    public Rating getRatingOfMovieByAccount(Movie movie, Account account) {
        Map<Integer, Rating> movieRatings = getMovieRatingMap(movie);
        if (!movieRatings.containsKey(account.getAccountId())) return null;
        return movieRatings.get(account.getAccountId());
    }

    @Override
    public Map<Integer, Long> getRatingStatistic(Movie movie) {
        Map<Integer, Rating> movieRatings = getMovieRatingMap(movie);
        Map<Integer, Long> result = movieRatings.values().stream().collect(groupingBy(Rating::getStar, counting()));
        for (int i = 1; i <= 5; i++) {
            result.putIfAbsent(i, 0L);
        }
        return result;
    }

    @Override
    public Rating editRating(Movie movie, Account author, Integer star, String content) {
        Map<Integer, Rating> movieRatings = getMovieRatingMap(movie);
        if (!movieRatings.containsKey(author.getAccountId())) throw new RuntimeException("Not exist rating!");
        Rating rating = movieRatings.get(author.getAccountId());
        if (star < 1 || star > 5) throw new RuntimeException("Star must in range 1 - 5");
        if (content == null || content.isEmpty()) throw new RuntimeException("Content must not be empty!");
        rating.setStar(star);
        rating.setContent(content);
        return rating;
    }

    @Override
    public void deleteRating(Movie movie, Account author) {
        Map<Integer, Rating> movieRatings = getMovieRatingMap(movie);
        if (!movieRatings.containsKey(author.getAccountId())) throw new RuntimeException("Not exist rating!");
        movieRatings.remove(author.getAccountId());
    }

    @Override
    public Page<Rating> getRatingsByMovie(Movie movie, Integer pageNumber, Integer pageSize) {
        Map<Integer, Rating> movieRatings = getMovieRatingMap(movie);
        List<Rating> items = movieRatings.values().stream().toList();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int start = pageNumber * pageSize;
        int end = Math.min((start + pageSize), items.size());
        return new PageImpl<>(items.subList(start, end), pageable, items.size());
    }
}
