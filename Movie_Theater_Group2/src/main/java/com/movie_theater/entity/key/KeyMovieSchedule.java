package com.movie_theater.entity.key;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class KeyMovieSchedule implements Serializable {
    private Integer movieId;
    private Integer scheduleId;
}
