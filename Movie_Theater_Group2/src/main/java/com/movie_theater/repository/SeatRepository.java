package com.movie_theater.repository;

import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.ScheduleSeatHis;
import com.movie_theater.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> getSeatByDeletedIsTrueAndCinemaRoom(CinemaRoom cinemaRoom);
    List<Seat> getSeatByCinemaRoom(CinemaRoom cinemaRoom);
    List<Seat> getSeatByDeletedIsFalseAndCinemaRoom(CinemaRoom cinemaRoom);
    Page<Seat> findByDeletedIsFalse(Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.deleted = :deleted WHERE s.seatId = :seatId")
    int updateDeletedBySeatId(@Param("deleted") boolean deleted, @Param("seatId") int seatId);

    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.deleted = :deleted WHERE " +
            "s.seatRow = :seatRow AND " +
            "s.seatColumn = :seatColumn AND " +
            "s.cinemaRoom = :cinemaRoom")
    int updateDeletedBySeatRowAndSeatColumnAndCinemaRoom(@Param("deleted") boolean deleted,
                                                         @Param("seatRow") int seatRow,
                                                         @Param("seatColumn") String seatColumn,
                                                         @Param("cinemaRoom") CinemaRoom cinemaRoom
    );

    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.seatType = :seatType WHERE s.seatId = :seatId")
    int updateSeatType(@Param("seatType") int seatType, @Param("seatId") int seatId);

    Seat getSeatBySeatId(int seatId);

}
