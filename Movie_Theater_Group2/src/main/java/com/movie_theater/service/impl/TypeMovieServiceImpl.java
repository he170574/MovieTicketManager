package com.movie_theater.service.impl;

import com.movie_theater.entity.TypeMovie;
import com.movie_theater.entity.key.*;
import com.movie_theater.repository.TypeMovieRepository;
import com.movie_theater.service.TypeMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TypeMovieServiceImpl implements TypeMovieService {
    private TypeMovieRepository typeMovieRepository;

    @Autowired
    public TypeMovieServiceImpl(TypeMovieRepository typeMovieRepository) {
        this.typeMovieRepository = typeMovieRepository;
    }

    @Override
    public List<TypeMovie> getAll() {
        return typeMovieRepository.findAll();
    }

    @Override
    public Optional<TypeMovie> getOne(KeyMovieType keyMovieType) {
        return typeMovieRepository.findById(keyMovieType);
    }

    @Override
    public TypeMovie save(TypeMovie typeMovie) {
        Optional<TypeMovie> optionalTypeMovie = getOne(typeMovie.getKeyMovieType());
        optionalTypeMovie.ifPresent(movie -> movie.setDeleted(Boolean.FALSE));
        return typeMovieRepository.save(typeMovie);
    }

    @Override
    public Boolean delete(KeyMovieType keyMovieType) {
        Optional<TypeMovie> optionalTypeMovie = getOne(keyMovieType);
        if (optionalTypeMovie.isPresent()) {
            TypeMovie typeMovie = optionalTypeMovie.get();
            typeMovieRepository.delete(typeMovie);
            return true;
        }
        return false;
    }
}
