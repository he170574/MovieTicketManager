package com.movie_theater.entity;

import lombok.*;

import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    private int star;

    private String content;

    private Movie movie;

    private Account author;

    private LocalTime createdTime;
}
