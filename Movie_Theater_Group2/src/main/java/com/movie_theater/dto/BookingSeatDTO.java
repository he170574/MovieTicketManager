package com.movie_theater.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.ScheduleSeat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingSeatDTO {
    private Integer movieId;
    private Integer scheduleId;
    private Integer seatId;
    private String seatColumn;
    private Integer seatRow;
    private Boolean seatStatus;
    private Boolean deleted;
    private Integer seatType;
    private Double price;
    private Integer scheduleSeatId;
}
