package com.movie_theater.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SeatDTO {
    private Integer cinemaRoomId;
    private Integer seatRow;
    private Integer seatNumber;
    private List<Integer> seatIdsBooked;
    private List<Integer> seatIdsCancelled;
    private List<String> seatNamesAdded;
    private List<String> seatNamesDeleted;
    private List<String> newSeatNamesAdded;
    private List<String> newSeatNamesDeleted;
    private List<Integer> seatIdsBookedVip;
    private List<Integer> seatIdsCancelledVip;
}
