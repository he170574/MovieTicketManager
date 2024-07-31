package com.movie_theater.dto;

import com.movie_theater.entity.MovieSchedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduleDTO {

    private Integer scheduleId;

    @NotNull(message = "Schedule time is required")
    private LocalDateTime scheduleTime;

    private Boolean deleted;

}
