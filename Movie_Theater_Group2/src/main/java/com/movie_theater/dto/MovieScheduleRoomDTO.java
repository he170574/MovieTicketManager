package com.movie_theater.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MovieScheduleRoomDTO {
    private Integer movieId;
    private String movieName;
    private String movieSmallImage;
    private Integer movieDuration;
    private Integer cinemaRoomId;
    private String cinemaRoomName;
    private Integer scheduleId;
    private LocalDateTime scheduleTime;
    private double priceNormalSeat;
    private double priceVipSeat;
    private double price;
    private Integer movieIdOld;
    private Integer scheduleIdOld;

    public MovieScheduleRoomDTO(Integer movieId, String movieName, String movieSmallImage, Integer movieDuration, Integer cinemaRoomId, String cinemaRoomName, Integer scheduleId, LocalDateTime scheduleTime) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.movieSmallImage = movieSmallImage;
        this.movieDuration = movieDuration;
        this.cinemaRoomId = cinemaRoomId;
        this.cinemaRoomName = cinemaRoomName;
        this.scheduleId = scheduleId;
        this.scheduleTime = scheduleTime;
    }

    public MovieScheduleRoomDTO(Integer movieId, String movieName, String movieSmallImage, Integer movieDuration, Integer cinemaRoomId, String cinemaRoomName, Integer scheduleId, LocalDateTime scheduleTime, double price) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.movieSmallImage = movieSmallImage;
        this.movieDuration = movieDuration;
        this.cinemaRoomId = cinemaRoomId;
        this.cinemaRoomName = cinemaRoomName;
        this.scheduleId = scheduleId;
        this.scheduleTime = scheduleTime;
        this.price = price;
    }
}
