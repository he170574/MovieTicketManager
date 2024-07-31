package com.movie_theater.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TypeDTO {

    private Integer typeId;

    @NotNull(message = "Type name is required")
    private String typeName;

    private Boolean deleted;
}
