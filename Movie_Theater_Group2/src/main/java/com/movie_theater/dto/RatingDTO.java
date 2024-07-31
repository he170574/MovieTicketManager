package com.movie_theater.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingDTO {
    int id;
    int star;
    String authorUsername;
    int authorAccountId;
    String content;
}
