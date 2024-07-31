package com.movie_theater.repository;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    void deleteByPromotionId(Integer id);
}
