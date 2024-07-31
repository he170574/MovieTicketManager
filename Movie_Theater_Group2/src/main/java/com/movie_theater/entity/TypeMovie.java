package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.movie_theater.entity.key.KeyMovieType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "TYPE_MOVIE", schema = "MOVIETHEATER")
public class TypeMovie {
    @EmbeddedId
    private KeyMovieType keyMovieType;

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOVIE_ID",
            foreignKey = @ForeignKey(name = "FK_MOVIE_TYPE_MOVIE"), nullable = false
    )
    @MapsId(value = "movieId")
    private Movie movie;

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_TYPE_TYPE_MOVIE"), nullable = false
    )
    @MapsId(value = "typeId")
    private Type type;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;
}
