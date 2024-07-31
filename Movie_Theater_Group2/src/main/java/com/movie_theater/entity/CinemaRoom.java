package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "CINEMA_ROOM", schema = "MOVIETHEATER")
public class CinemaRoom {
    @Id
    @Column(name = "CINEMA_ROOM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cinemaRoomId;

    @Nationalized
    @Column(name = "CINEMA_ROOM_NAME", nullable = false)
    private String cinemaRoomName;

    @Column(name = "SEAT_QUANTITY", nullable = false)
    private Integer seatQuantity;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @ToString.Exclude
    @JsonBackReference
    @OneToMany(mappedBy = "cinemaRoom", fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();
}
