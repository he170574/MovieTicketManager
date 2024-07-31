package com.movie_theater.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FOOD", schema = "MOVIETHEATER")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_ID")
    private Integer foodId;
    @Column(name = "FOOD_NAME")
    private String foodName;
    @Column(name = "FOOD_SIZE")
    private String foodSize;
    @Column(name = "FOOD_PRICE")
    private float foodPrice;
    @Column(name = "FOOD_IMAGE")
    private String foodImage;
    @Column(name = "DELETED")
    private boolean deleted;
}