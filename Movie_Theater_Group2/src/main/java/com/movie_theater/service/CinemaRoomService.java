package com.movie_theater.service;

import com.movie_theater.entity.CinemaRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CinemaRoomService {
    List<CinemaRoom> getCinemaRoomByDeletedIsFalse();
    CinemaRoom save(CinemaRoom cinemaRoom);

    Page<CinemaRoom> findByCinemaRoomNameContainingAndDeletedIsFalse(String name, Pageable pageable);

    int updateSeatQuantity(CinemaRoom cinemaRoom);
    int updateDeleted(boolean deleted, int cinemaRoomId);

    Optional<CinemaRoom> getCinemaRoomByCinemaRoomIdAndDeletedIsFalse(Integer cinemaRoomId);
    CinemaRoom getCinemaRoomByCinemaRoomId(int cinemaRoomId);
    int updateCinemaRoomName(String cinemaRoomName, int cinemaRoomId);
}
