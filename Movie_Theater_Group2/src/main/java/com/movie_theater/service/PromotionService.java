package com.movie_theater.service;

import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.entity.Promotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    List<Promotion> getAll();

    Optional<Promotion> getOne(Integer id);

    Promotion save(Promotion promotion);

    Promotion parsePromotionDtoToPromotion(PromotionDTO promotionDTO);

    PromotionDTO parseEntityToDto(Promotion promotion);

    void deleteByPromotionId(Integer id);

    Promotion updatePromotion(Integer id, String detail, Integer discountLevel, LocalDateTime endTime, LocalDateTime startTime, String title,String code);
}
