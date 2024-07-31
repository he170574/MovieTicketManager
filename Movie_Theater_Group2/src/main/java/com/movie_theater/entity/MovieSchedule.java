package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.movie_theater.entity.key.KeyMovieSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "MOVIE_SCHEDULE", schema = "MOVIETHEATER")
public class MovieSchedule {
    @EmbeddedId
    private KeyMovieSchedule keyMovieSchedule;

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOVIE_ID",
            foreignKey = @ForeignKey(name = "FK_MOVIE_MOVIE_SCHEDULE"), nullable = false
    )
    @MapsId(value = "movieId")
    private Movie movie;

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID",
            foreignKey = @ForeignKey(name = "FK_SCHEDULE_MOVIE_SCHEDULE"), nullable = false
    )
    @MapsId(value = "scheduleId")
    private Schedule schedule;

    @ToString.Exclude
    @OneToMany(mappedBy = "movieSchedule")
    private List<ScheduleSeat> scheduleSeats;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

}
