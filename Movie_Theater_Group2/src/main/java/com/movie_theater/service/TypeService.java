package com.movie_theater.service;

import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.dto.TypeDTO;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.Type;
import com.movie_theater.entity.TypeMovie;

import java.util.List;
import java.util.Optional;

public interface TypeService {

    List<Type> getAll();

    Optional<Type> getOne(Integer id);

    Type save(Type type);

    Type parseTypeDtoToType(TypeDTO typeDTO);

    TypeDTO parseTypeToTypeDto(Type type);

    Boolean delete(Integer id);
}
