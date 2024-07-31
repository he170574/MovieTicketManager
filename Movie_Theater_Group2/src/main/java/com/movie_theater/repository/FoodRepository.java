package com.movie_theater.repository;

import com.movie_theater.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FoodRepository extends JpaRepository<Food,Integer> {


    @Transactional
    Page<Food> getAllByFoodNameContainsAndDeletedIsFalse(String foodName, Pageable pageable);

    @Query
    Food getByFoodIdAndDeletedIsFalse(Integer foodId);

    @Query("UPDATE Food f SET f.deleted = true WHERE f.foodId = :foodId")
    void deActiveFoodByFoodId (Integer foodId);
}