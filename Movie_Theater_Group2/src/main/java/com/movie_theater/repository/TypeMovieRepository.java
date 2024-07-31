package com.movie_theater.repository;

import com.movie_theater.entity.Movie;
import com.movie_theater.entity.TypeMovie;
import com.movie_theater.entity.key.KeyMovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TypeMovieRepository extends JpaRepository<TypeMovie, KeyMovieType> {
    @Override
    @Modifying
    @Query("SELECT t FROM TypeMovie t WHERE t.deleted = false")
    List<TypeMovie> findAll();

    @Override
    @Query("SELECT t FROM TypeMovie t WHERE t.keyMovieType = :id AND t.deleted = false")
    Optional<TypeMovie> findById(@Param("id") KeyMovieType id);

    Integer deleteTypeMovieByMovie(Movie movie);
}
