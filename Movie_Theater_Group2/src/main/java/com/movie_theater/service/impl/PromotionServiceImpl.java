package com.movie_theater.service.impl;

import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.TypeMovie;
import com.movie_theater.repository.PromotionRepository;
import com.movie_theater.service.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private PromotionRepository promotionRepository;

    @Autowired
    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> getOne(Integer id) {
        return promotionRepository.findById(id);
    }

    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion parsePromotionDtoToPromotion(PromotionDTO promotionDTO) {
        return Promotion.builder()
                .promotionId(promotionDTO.getPromotionId())
                .deleted(false)
                .detail(promotionDTO.getDetail())
                .discountLevel(promotionDTO.getDiscountLevel())
                .endTime(promotionDTO.getEndTime())
                .startTime(promotionDTO.getStartTime())
                .title(promotionDTO.getTitle())
                .code(promotionDTO.getCode())
                .build();
    }

    @Override
    public PromotionDTO parseEntityToDto(Promotion promotion) {
        return PromotionDTO.builder()
                .promotionId(promotion.getPromotionId())
                .detail(promotion.getDetail())
                .discountLevel(promotion.getDiscountLevel())
                .endTime(promotion.getEndTime())
                .startTime(promotion.getStartTime())
                .title(promotion.getTitle())
                .code(promotion.getCode())
                .build();
    }

    @Override
    public void deleteByPromotionId(Integer id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Promotion not found with ID: " + id);
        }
    }


    @Override
    @Transactional
    public Promotion updatePromotion(Integer id, String detail, Integer discountLevel, LocalDateTime endTime, LocalDateTime startTime, String title, String code) {
        Optional<Promotion> existingPromotion = promotionRepository.findById(id);
        if (existingPromotion.isPresent()) {
            Promotion promotion = existingPromotion.get();
            promotion.setDetail(detail);
            promotion.setDiscountLevel(discountLevel);
            promotion.setEndTime(endTime);
            promotion.setStartTime(startTime);
            promotion.setTitle(title);
            promotion.setCode(code);
            return promotionRepository.save(promotion);
        } else {
            throw new EntityNotFoundException("Promotion not found with ID: " + id);
        }
    }



}
