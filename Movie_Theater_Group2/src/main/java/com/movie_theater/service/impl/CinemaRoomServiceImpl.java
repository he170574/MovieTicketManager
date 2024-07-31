package com.movie_theater.service.impl;

import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Seat;
import com.movie_theater.repository.CinemaRoomRepository;
import com.movie_theater.repository.SeatRepository;
import com.movie_theater.service.CinemaRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CinemaRoomServiceImpl implements CinemaRoomService {

    private CinemaRoomRepository cinemaRoomRepository;
    private SeatRepository seatRepository;


    @Autowired
    public CinemaRoomServiceImpl(CinemaRoomRepository cinemaRoomRepository, SeatRepository seatRepository) {
        this.cinemaRoomRepository = cinemaRoomRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<CinemaRoom> getCinemaRoomByDeletedIsFalse() {
        return cinemaRoomRepository.getCinemaRoomByDeletedIsFalse();
    }

    @Override
    public CinemaRoom save(CinemaRoom cinemaRoom) {
        return cinemaRoomRepository.save(cinemaRoom);
    }

    @Override
    public Page<CinemaRoom> findByCinemaRoomNameContainingAndDeletedIsFalse(String name, Pageable pageable) {
        return cinemaRoomRepository.findByCinemaRoomNameContainingAndDeletedIsFalse(name, pageable);
    }

    @Override
    public int updateSeatQuantity(CinemaRoom cinemaRoom) {
        List<Seat> seats = seatRepository.getSeatByDeletedIsFalseAndCinemaRoom(cinemaRoom);
        cinemaRoomRepository.updateSeatQuantity(seats.size(), cinemaRoom.getCinemaRoomId());
        return 0;
    }

    @Override
    public int updateDeleted(boolean deleted, int cinemaRoomId) {
        return cinemaRoomRepository.updateDeleted(deleted, cinemaRoomId);
    }

    @Override
    public Optional<CinemaRoom> getCinemaRoomByCinemaRoomIdAndDeletedIsFalse(Integer cinemaRoomId) {
        return cinemaRoomRepository.getCinemaRoomByCinemaRoomIdAndDeletedIsFalse(cinemaRoomId);
    }

    @Override
    public CinemaRoom getCinemaRoomByCinemaRoomId(int cinemaRoomId) {
        return cinemaRoomRepository.getCinemaRoomByCinemaRoomId(cinemaRoomId);
    }

    @Override
    public int updateCinemaRoomName(String cinemaRoomName, int cinemaRoomId) {
        return cinemaRoomRepository.updateCinemaRoomName(cinemaRoomName, cinemaRoomId);
    }

}
