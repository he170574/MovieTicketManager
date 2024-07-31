package com.movie_theater.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Integer promotionId;

    @NotNull(message = "Detail is required")
    private String detail;

    @Min(0)
    @Max(100)
    @NotNull(message = "Discount level is required")
    private Integer discountLevel;

    @FutureOrPresent(message = "")
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;


    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time should be future")
    private LocalDateTime startTime;

    @NotNull(message = "fail to gen code")
    private String code;

    @NotNull(message = "Title is required")
    private String title;
    private Boolean deleted;
}
