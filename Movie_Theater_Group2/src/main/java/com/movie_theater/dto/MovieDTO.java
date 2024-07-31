package com.movie_theater.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MovieDTO {
    private Integer movieId;
    private String actor;
    private String content;
    private String director;
    private Integer duration;
    private String movieProductionCompany;
    private String movieName;
    private String largeImage;
    private String smallImage;
    private Boolean deleted;
    private List<LocalDateTime> movieSchedules;
    private List<Integer> typeMovies;
    private List<String> typeMoviesString;
    private String introVideo;
}