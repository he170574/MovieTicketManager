package com.movie_theater.service.impl;

import com.movie_theater.dto.MovieDTO;
import com.movie_theater.entity.Movie;
import com.movie_theater.entity.MovieSchedule;
import com.movie_theater.entity.TypeMovie;
import com.movie_theater.repository.MovieRepository;
import com.movie_theater.repository.TypeMovieRepository;
import com.movie_theater.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private TypeMovieRepository typeMovieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, TypeMovieRepository typeMovieRepository) {
        this.movieRepository = movieRepository;
        this.typeMovieRepository = typeMovieRepository;
    }

    @Override
    public Optional<Movie> getById(Integer id) {
        return movieRepository.findById(id);
    }

    @Override
    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie update(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Boolean delete(Integer id) {
        Optional<Movie> optionalMovie = getById(id);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            movie.setDeleted(Boolean.TRUE);
            return true;
        }
        return false;
    }

    @Override
    public Movie parseMovieDtoToMovie(MovieDTO movieDTO) {
        return Movie.builder()
                .movieId(movieDTO.getMovieId())
                .actor(movieDTO.getActor())
                .content(movieDTO.getContent())
                .deleted(false)
                .director(movieDTO.getDirector())
                .duration(movieDTO.getDuration())
                .introVideo(movieDTO.getIntroVideo())
                .largeImage(movieDTO.getLargeImage())
                .smallImage(movieDTO.getSmallImage())
                .movieName(movieDTO.getMovieName())
                .movieProductionCompany(movieDTO.getMovieProductionCompany())
                .movieSchedules(getAllMovieScheduleByMovieId(movieDTO.getMovieId()))
                .typeMovies(new ArrayList<>())
                .build();
    }

    @Override
    public List<TypeMovie> getAllTypeMovieByMovieId(Integer id) {
        if (id == null) {
            return new ArrayList<>();
        }
        Optional<Movie> movieOptional = getById(id);
        // Filter out TypeMovies with deleted = false
        return movieOptional.map(movie -> movie.getTypeMovies()
                .stream()
                .filter(typeMovie -> !typeMovie.getDeleted())
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    @Override
    public List<MovieSchedule> getAllMovieScheduleByMovieId(Integer id) {
        if (id == null) {
            return new ArrayList<>();
        }

        Optional<Movie> movieOptional = getById(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            // Giả sử Movie có một danh sách MovieSchedule, bạn có thể trả về nó như sau:
            return movie.getMovieSchedules();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Integer deleteTypeMovieByMovie(Movie movie) {
        return typeMovieRepository.deleteTypeMovieByMovie(movie);
    }

    @Override
    public MovieDTO parseMovieToMovieDto(Movie movie) {
        return MovieDTO.builder()
                .movieId(movie.getMovieId())
                .actor(movie.getActor())
                .content(movie.getContent())
                .movieName(movie.getMovieName())
                .movieProductionCompany(movie.getMovieProductionCompany())
                .director(movie.getDirector())
                .duration(movie.getDuration())
                .introVideo(movie.getIntroVideo())
                .smallImage(movie.getSmallImage())
                .largeImage(movie.getLargeImage())
                .introVideo(movie.getIntroVideo())
                .typeMovies(movie.getTypeMovies().stream()
                        .map(typeMovie -> typeMovie.getType().getTypeId())
                        .collect(Collectors.toList()))
                .movieSchedules(movie.getMovieSchedules().stream()
                        .map(movieSchedule -> movieSchedule.getSchedule().getScheduleTime())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Page<Movie> getAllMovieForMember(Pageable pageable) {
        return movieRepository.getMovieByDeletedIsFalse(pageable);
    }

    @Override
    public Page<Movie> findByKeywordAndNotDeleted(String keyword, Pageable pageable) {
        return movieRepository.findByKeywordAndNotDeleted(keyword, pageable);
    }

    @Override
    public int updateDeleted(boolean deleted, int movieId) {
        return movieRepository.updateDeleted(deleted, movieId);
    }
    @Override
    public List<Movie> getMoviePlayingByDate(LocalDate date) {
        return movieRepository.getMoviePlayingByDate(date);
    }

    @Override
    public Page<Movie> getMovieByMovieName(String movieName,Pageable pageable) {
        return movieRepository.findByMovieNameContainsAndDeletedIsFalse(movieName,pageable);
    }
}