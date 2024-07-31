package com.movie_theater.repository;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    @Override
    @Modifying
    @Query("SELECT m FROM Movie m WHERE m.deleted = false")
    List<Movie> findAll();

    @Query("SELECT m FROM Movie m WHERE " +
            "LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND m.deleted = false")
    Page<Movie> findByKeywordAndNotDeleted(String keyword, Pageable pageable);
    @Override
    @Query("SELECT m FROM Movie m WHERE m.movieId = :id AND m.deleted = false")
    Optional<Movie> findById(@Param("id") Integer id);

    Page<Movie> getMovieByDeletedIsFalse(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.deleted = :deleted where m.movieId = :movieId")
    int updateDeleted(@Param("deleted") boolean deleted, @Param("movieId") int movieId);
    @Query("SELECT M FROM Movie M JOIN M.movieSchedules MS JOIN MS.schedule S WHERE cast(S.scheduleTime as date) = :date AND M.deleted = false AND MS.deleted = false AND S.deleted = false ")
    List<Movie> getMoviePlayingByDate(LocalDate date);

    Page<Movie> findByMovieNameContainsAndDeletedIsFalse(String movieNameEnglish,Pageable pageable);

}