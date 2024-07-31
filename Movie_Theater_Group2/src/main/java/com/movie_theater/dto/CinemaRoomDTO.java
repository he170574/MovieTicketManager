package com.movie_theater.dto;

import lombok.*;

import java.util.List;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CinemaRoomDTO {
    private Integer id;
    private String cinemaRoomName;
    private List<SeatDTO> seatDTOS;
}
