package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(
        name = "SCHEDULE_SEAT",
        schema = "MOVIETHEATER",
        uniqueConstraints = {
                @UniqueConstraint(name = "UNIQUE_SEAT_MOVIE_SCHEDULE", columnNames = {"SEAT_ID", "MOVIE_ID", "SCHEDULE_ID"})
        }
)
public class ScheduleSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_SEAT_ID")
    private Integer scheduleSeatId;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "STATUS", nullable = false)
    private Boolean status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "MOVIE_ID", referencedColumnName = "MOVIE_ID", foreignKey = @ForeignKey(name = "FK_SCHEDULE_SEAT_MOVIE_SCHEDULE"), nullable = false),
            @JoinColumn(name = "SCHEDULE_ID", referencedColumnName = "SCHEDULE_ID", foreignKey = @ForeignKey(name = "FK_SCHEDULE_SEAT_MOVIE_SCHEDULE"), nullable = false)
    })
    @JsonBackReference
    @ToString.Exclude
    private MovieSchedule movieSchedule;
    //
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAT_ID",
            foreignKey = @ForeignKey(name = "FK_SEAT_SCHEDULE_SEAT"), nullable = false
    )
    @JsonBackReference
    private Seat seat;
}
