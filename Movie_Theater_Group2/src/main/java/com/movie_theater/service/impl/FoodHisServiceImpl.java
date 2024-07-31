package com.movie_theater.service.impl;


import com.movie_theater.entity.FoodHis;
import com.movie_theater.repository.FoodHisRepository;
import com.movie_theater.service.FoodHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodHisServiceImpl implements FoodHisService {
    @Autowired
    FoodHisRepository foodHisRepository;
    @Override
    public FoodHis save(FoodHis foodHis) {
        return foodHisRepository.save(foodHis);
    }
}
