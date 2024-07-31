package com.movie_theater.repository;
import com.movie_theater.entity.CinemaRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CinemaRoomRepository extends JpaRepository<CinemaRoom, Integer> {
    List<CinemaRoom> getCinemaRoomByDeletedIsFalse();

    Optional<CinemaRoom> getCinemaRoomByCinemaRoomIdAndDeletedIsFalse(Integer cinemaRoomId);

    Page<CinemaRoom> findByCinemaRoomNameContainingAndDeletedIsFalse(String name, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE CinemaRoom c SET c.seatQuantity = :seatQuantity where c.cinemaRoomId = :cinemaRoomId")
    int updateSeatQuantity(@Param("seatQuantity") int seatQuantity, @Param("cinemaRoomId") int cinemaRoomId);

    @Modifying
    @Transactional
    @Query("UPDATE CinemaRoom c SET c.deleted = :deleted where c.cinemaRoomId = :cinemaRoomId")
    int updateDeleted(@Param("deleted") boolean deleted, @Param("cinemaRoomId") int cinemaRoomId);

    @Modifying
    @Transactional
    @Query("UPDATE CinemaRoom c SET c.cinemaRoomName = :cinemaRoomName where c.cinemaRoomId = :cinemaRoomId")
    int updateCinemaRoomName(@Param("cinemaRoomName") String cinemaRoomName, @Param("cinemaRoomId") int cinemaRoomId);
    CinemaRoom getCinemaRoomByCinemaRoomId(int cinemaRoomId);

}
