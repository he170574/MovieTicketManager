package com.movie_theater.dto;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDTO {
    private int foodId;
    private String foodName;
    private String foodSize;
    private float foodPrice;
    private String foodImage;
    private MultipartFile foodImageFile;
    private Integer quantity;
}