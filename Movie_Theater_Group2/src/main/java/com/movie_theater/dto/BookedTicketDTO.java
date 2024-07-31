package com.movie_theater.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedTicketDTO {
    private String movieName;
    private LocalDateTime bookingDate;
    private Integer amount;
    private boolean status;
}
