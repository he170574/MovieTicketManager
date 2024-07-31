package com.movie_theater.service;

import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.Type;
import com.movie_theater.entity.TypeMovie;
import com.movie_theater.entity.key.KeyMovieType;

import java.util.List;
import java.util.Optional;

public interface TypeMovieService {
    List<TypeMovie> getAll();

    Optional<TypeMovie> getOne(KeyMovieType keyMovieType);

    TypeMovie save(TypeMovie typeMovie);

    Boolean delete(KeyMovieType keyMovieType);
}
