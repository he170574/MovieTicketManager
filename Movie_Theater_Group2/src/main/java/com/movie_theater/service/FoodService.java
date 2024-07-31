package com.movie_theater.service;


import com.movie_theater.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FoodService {
    Food save(Food food);

    Page<Food> getAllByFoodNameContainsAndDeletedIsFalse(String foodName, Pageable pageable);

    Food getById(Integer foodId);

    void deActiveFoodByFoodId(Integer foodId);
}