package com.movie_theater.dto;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieHomeDTO {
    private Integer movieId;
    private String actor;
    private String content;
    private String director;
    private Integer duration;
    private String movieProductionCompany;
    private String movieNameEnglish;
    private String movieNameVietnamese;
    private LocalDate toDate;
    private String movieName;
    private String largeImage;
    private String smallImage;
    private Boolean deleted;
    private List<LocalDateTime> movieSchedules;
    private List<String> typeMovies;
}