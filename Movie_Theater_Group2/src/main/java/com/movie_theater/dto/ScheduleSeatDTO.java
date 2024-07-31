package com.movie_theater.dto;

import com.movie_theater.entity.MovieSchedule;
import com.movie_theater.entity.Seat;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduleSeatDTO {
    private Integer scheduleSeatId;

    private Double price;

    private Integer movieId;

    private Integer scheduleId;

    private Integer seatId;

    private Integer seatRow;

    private String seatColumn;

    private boolean selected;
}
