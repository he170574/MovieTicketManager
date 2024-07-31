package com.movie_theater.repository;

import com.movie_theater.dto.MovieScheduleRoomDTO;
import com.movie_theater.entity.MovieSchedule;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.Schedule;
import com.movie_theater.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Integer> {

    @Transactional
    @Query("SELECT new com.movie_theater.dto.MovieScheduleRoomDTO (" +
            "m.movieId" +
            ", m.movieName" +
            ", m.smallImage" +
            ", m.duration" +
            ", cr.cinemaRoomId" +
            ", cr.cinemaRoomName" +
            ", sh.scheduleId" +
            ", sh.scheduleTime) " +
            "FROM Schedule sh " +
            "JOIN sh.movieSchedules ms " +
            "JOIN ms.movie m " +
            "JOIN ms.scheduleSeats ss " +
            "JOIN ss.seat s " +
            "JOIN s.cinemaRoom cr " +
            "WHERE m.deleted = false " +
            "GROUP BY m.movieId, m.movieName, m.smallImage, m.duration, cr.cinemaRoomId, cr.cinemaRoomName, sh.scheduleId, sh.scheduleTime, m.deleted")
    List<MovieScheduleRoomDTO> getAllMovieScheduleRoom();

    @Transactional
    @Query("SELECT new com.movie_theater.dto.MovieScheduleRoomDTO (" +
            "m.movieId" +
            ", m.movieName" +
            ", m.smallImage" +
            ", m.duration" +
            ", cr.cinemaRoomId" +
            ", cr.cinemaRoomName" +
            ", sh.scheduleId" +
            ", sh.scheduleTime)" +
            "FROM Schedule sh " +
            "JOIN sh.movieSchedules ms " +
            "JOIN ms.movie m " +
            "JOIN ms.scheduleSeats ss " +
            "JOIN ss.seat s " +
            "JOIN s.cinemaRoom cr " +
            "WHERE m.deleted = false AND cr.cinemaRoomId = :cinemaRoomId AND cast(sh.scheduleTime as date) = :date " +
            "GROUP BY m.movieId, m.movieName, m.smallImage, m.duration, cr.cinemaRoomId, cr.cinemaRoomName, sh.scheduleId, sh.scheduleTime, m.deleted")
    List<MovieScheduleRoomDTO> getAllMovieScheduleRoomByCinemaRoomIdAndDate(Integer cinemaRoomId, LocalDate date);

    @Transactional
    @Query("SELECT new com.movie_theater.dto.MovieScheduleRoomDTO (" +
            "m.movieId" +
            ", m.movieName" +
            ", m.smallImage" +
            ", m.duration" +
            ", cr.cinemaRoomId" +
            ", cr.cinemaRoomName" +
            ", sh.scheduleId" +
            ", sh.scheduleTime)" +
            "FROM Schedule sh " +
            "JOIN sh.movieSchedules ms " +
            "JOIN ms.movie m " +
            "JOIN ms.scheduleSeats ss " +
            "JOIN ss.seat s " +
            "JOIN s.cinemaRoom cr " +
            "WHERE m.deleted = false AND cr.cinemaRoomId = :cinemaRoomId AND sh.scheduleId = :scheduleId " +
            "GROUP BY m.movieId, m.movieName, m.smallImage, m.duration, cr.cinemaRoomId, cr.cinemaRoomName, sh.scheduleId, sh.scheduleTime, m.deleted")
    List<MovieScheduleRoomDTO> getAllMovieScheduleRoomByCinemaRoomIdScheduleId(Integer cinemaRoomId,  Integer scheduleId);

    @Modifying
    @Transactional
    @Query("UPDATE CinemaRoom c SET c.seatQuantity = :seatQuantity where c.cinemaRoomId = :cinemaRoomId")
    int updateSeatQuantity(@Param("seatQuantity") int seatQuantity, @Param("cinemaRoomId") int cinemaRoomId);

    @Modifying
    @Transactional
    @Query("UPDATE CinemaRoom c SET c.deleted = :deleted where c.cinemaRoomId = :cinemaRoomId")
    int updateDeleted(@Param("deleted") boolean deleted, @Param("cinemaRoomId") int cinemaRoomId);

    List<ScheduleSeat> getByMovieSchedule(MovieSchedule movieSchedule);

    @Query(value = "SELECT TOP(1) ss.PRICE " +
            "FROM MOVIETHEATER.SCHEDULE_SEAT ss " +
            "JOIN MOVIETHEATER.SEAT s ON ss.SEAT_ID = s.SEAT_ID " +
            "WHERE s.SEAT_TYPE = :seatType AND ss.MOVIE_ID = :movieId AND ss.SCHEDULE_ID = :scheduleId AND ss.DELETED = 0",nativeQuery = true)
    Integer getPriceBySeatTypeAndScheduleIdAndMovieId(@Param("seatType") int seatType,
                                                      @Param("movieId") int movieId,
                                                      @Param("scheduleId") int scheduleId);

    void deleteByMovieSchedule(MovieSchedule movieSchedule);

    @Modifying
    @Transactional
    @Query("UPDATE ScheduleSeat s SET s.status = :status where s.scheduleSeatId = :scheduleSeatId")
    int updateScheduleSeatStatus(@Param("status") boolean status, @Param("scheduleSeatId") int scheduleSeatId);
}
