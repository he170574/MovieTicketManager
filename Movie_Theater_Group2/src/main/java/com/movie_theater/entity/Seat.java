package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "SEAT", schema = "MOVIETHEATER",
        uniqueConstraints = @UniqueConstraint(columnNames = {"SEAT_COLUMN", "SEAT_ROW", "CINEMA_ROOM_ID"}))
public class Seat {
    @Id
    @Column(name = "SEAT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seatId;

    @Nationalized
    @Column(name = "SEAT_COLUMN", nullable = false)
    private String seatColumn;

    @Column(name = "SEAT_ROW", nullable = false)
    private Integer seatRow;

//    @Column(name = "SEAT_STATUS", nullable = false)
//    private Integer seatStatus;

    @Column(name = "SEAT_TYPE", nullable = false)
    private Integer seatType;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="CINEMA_ROOM_ID",
            foreignKey = @ForeignKey(name = "FK_SEAT_CINEMA_ROOM"), nullable = false
    )
    @JsonBackReference
    private CinemaRoom cinemaRoom;

    @ToString.Exclude
    @OneToMany(mappedBy = "seat",fetch = FetchType.LAZY)
    @JsonBackReference
    private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

}
