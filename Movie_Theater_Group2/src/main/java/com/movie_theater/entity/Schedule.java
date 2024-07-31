package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "SCHEDULE", schema = "MOVIETHEATER")
public class Schedule {
    @Id
    @Column(name = "SCHEDULE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @Column(name = "SCHEDULE_TIME", nullable = false)
    private LocalDateTime scheduleTime;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @JsonManagedReference
    @ToString.Exclude
    @OneToMany(mappedBy = "schedule")
    private List<MovieSchedule> movieSchedules = new ArrayList<>();
}
