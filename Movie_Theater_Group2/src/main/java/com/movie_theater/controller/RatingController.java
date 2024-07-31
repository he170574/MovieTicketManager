package com.movie_theater.controller;

import com.movie_theater.dto.RatingDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.Rating;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.AccountService;
import com.movie_theater.service.MovieService;
import com.movie_theater.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class RatingController {

    private final MovieService movieService;
    private final RatingService ratingService;
    private final AccountService accountService;

    public RatingController(MovieService movieService, RatingService ratingService, AccountService accountService) {
        this.movieService = movieService;
        this.ratingService = ratingService;
        this.accountService = accountService;
    }

    @GetMapping("/movie-rating-statistic")
    public ResponseEntity<ResponseDTO> getRatingStatistic(@RequestParam Integer movieId) {
        Movie movie = movieService.getById(movieId).orElseThrow();
        return ResponseEntity.ok(ResponseDTO.builder().data(ratingService.getRatingStatistic(movie)).build());
    }

    @GetMapping("/get-rating-of-movie-by-current-account")
    public ResponseEntity<ResponseDTO> getRatingOfCurrentAccount(@RequestParam Integer movieId,
                                                                 Authentication authentication) {
        Movie movie = movieService.getById(movieId).orElseThrow();
        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
        Account account = accountService.getByUsername(customAccount.getUsername());
        Rating result = ratingService.getRatingOfMovieByAccount(movie, account);
        RatingDTO dto = null;
        if (result != null) dto = RatingDTO.builder()
                .star(result.getStar())
                .content(result.getContent())
                .authorUsername(result.getAuthor().getUsername())
                .authorAccountId(result.getAuthor().getAccountId())
                .build();
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("success")
                .data(dto).build());
    }


    @GetMapping("/delete-rating")
    public ResponseEntity<ResponseDTO> deleteRating(@RequestParam Integer movieId,
                                                    Authentication authentication) {
        Movie movie = movieService.getById(movieId).orElseThrow();
        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
        Account account = accountService.getByUsername(customAccount.getUsername());
        ratingService.deleteRating(movie, account);
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("success").build());
    }

    @PostMapping("/create-rating")
    public ResponseEntity<ResponseDTO> createRating(@RequestParam Integer movieId,
                                                    @RequestParam String content,
                                                    @RequestParam Integer star,
                                                    Authentication authentication) {
        Movie movie = movieService.getById(movieId).orElseThrow();
        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
        Account account = accountService.getByUsername(customAccount.getUsername());
        Rating result = ratingService.createRating(movie, account, star, content);
        RatingDTO dto = RatingDTO.builder()
                .star(result.getStar())
                .content(result.getContent())
                .authorUsername(result.getAuthor().getUsername())
                .authorAccountId(result.getAuthor().getAccountId())
                .build();
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("Rating successfully created!")
                .data(dto).build());
    }

    @GetMapping("/get-movie-rating")
    public ResponseEntity<ResponseDTO> getMovieRating(@RequestParam Integer movieId,
                                                      @RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize) {
        Movie movie = movieService.getById(movieId).orElseThrow();
        Page<Rating> data = ratingService.getRatingsByMovie(movie, pageNumber, pageSize);
        List<RatingDTO> dto = data.stream().map(e ->
                RatingDTO.builder()
                        .star(e.getStar())
                        .content(e.getContent())
                        .authorUsername(e.getAuthor().getUsername())
                        .authorAccountId(e.getAuthor().getAccountId())
                        .build()).toList();

        return ResponseEntity.ok(ResponseDTO.builder().data(Map.of(
                "items", dto,
                "totalPages", data.getTotalPages()
        )).build());
    }
}
