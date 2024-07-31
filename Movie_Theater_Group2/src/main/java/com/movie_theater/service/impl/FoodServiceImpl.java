package com.movie_theater.service.impl;

import com.movie_theater.entity.Food;
import com.movie_theater.repository.FoodRepository;
import com.movie_theater.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    FoodRepository foodRepository;

    @Override
    public Food save(Food food) {
        return foodRepository.save(food);
    }

    @Override
    public Page<Food> getAllByFoodNameContainsAndDeletedIsFalse(String foodName, Pageable pageable) {
        return foodRepository.getAllByFoodNameContainsAndDeletedIsFalse(foodName,pageable);
    }

    @Override
    public Food getById(Integer foodId) {
        return foodRepository.getByFoodIdAndDeletedIsFalse(foodId);
    }

    @Override
    public void deActiveFoodByFoodId(Integer foodId) {
        foodRepository.deActiveFoodByFoodId(foodId);
    }
}