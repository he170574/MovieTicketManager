package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "MOVIE", schema = "MOVIETHEATER")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOVIE_ID")
    private Integer movieId;

    @Nationalized
    @Column(name = "ACTOR", nullable = false)
    private String actor;

    @Nationalized
    @Column(name = "CONTENT", nullable = false, length = 2000)
    private String content;

    @Nationalized
    @Column(name = "DIRECTOR", nullable = false)
    private String director;

    @Column(name = "DURATION", nullable = false)
    private Integer duration;

    @Nationalized
    @Column(name = "MOVIE_PRODUCTION_COMPANY", nullable = false)
    private String movieProductionCompany;

    @Nationalized
    @Column(name = "MOVIE_NAME", nullable = false)
    private String movieName;

    @Nationalized
    @Column(name = "LARGE_IMAGE", nullable = false)
    private String largeImage;

    @Nationalized
    @Column(name = "SMALL_IMAGE", nullable = false)
    private String smallImage;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @JsonManagedReference
    @ToString.Exclude
    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<MovieSchedule> movieSchedules;

    @JsonManagedReference
    @ToString.Exclude
    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<TypeMovie> typeMovies;

    @Column(name = "INTRO_VIDEO")
    private String introVideo;
}