package com.movie_theater.repository;

import com.movie_theater.entity.ScheduleSeatHis;
import com.movie_theater.entity.Type;
import com.movie_theater.entity.TypeMovie;
import com.movie_theater.entity.key.KeyMovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type, Integer> {

    @Override
    @Modifying
    @Query("SELECT t FROM Type t WHERE t.deleted = false")
    List<Type> findAll();

    @Override
    @Query("SELECT t FROM Type t WHERE t.typeId = :id AND t.deleted = false")
    Optional<Type> findById(@Param("id") Integer id);
}
