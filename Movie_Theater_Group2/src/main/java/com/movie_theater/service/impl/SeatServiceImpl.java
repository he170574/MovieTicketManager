package com.movie_theater.service.impl;

import com.movie_theater.dto.BookingSeatDTO;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.ScheduleSeat;
import com.movie_theater.entity.Seat;
import com.movie_theater.repository.SeatRepository;
import com.movie_theater.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {
    private SeatRepository seatRepository;

    @Autowired
    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional
    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    public List<Seat> getSeatByCinemaRoom(CinemaRoom cinemaRoom) {
        return seatRepository.getSeatByCinemaRoom(cinemaRoom);
    }

    @Override
    public List<Seat> pagingSeatByDeletedIsFalse(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Seat> seats = seatRepository.findByDeletedIsFalse(pageable);
        return seats.getContent();
    }

    @Override
    public List<Seat> getSeatByDeletedIsFalseAndCinemaRoom(CinemaRoom cinemaRoom) {
        return seatRepository.getSeatByDeletedIsFalseAndCinemaRoom(cinemaRoom);
    }

    @Override
    public int updateDeletedBySeatId(boolean deleted, int seatId) {
        return seatRepository.updateDeletedBySeatId(deleted, seatId);
    }

    @Override
    public int updateDeletedBySeatRowAndSeatColumnAndCinemaRoom(boolean deleted, int seatRow, String seatColumn, CinemaRoom cinemaRoom) {
        return seatRepository.updateDeletedBySeatRowAndSeatColumnAndCinemaRoom(deleted, seatRow, seatColumn, cinemaRoom);
    }


    @Override
    public int updateSeatType(int seatType, int seatId) {
        return seatRepository.updateSeatType(seatType, seatId);
    }

    @Override
    public Seat getSeatBySeatId(int seatId) {
        return seatRepository.getSeatBySeatId(seatId);
    }

    @Override
    public List<BookingSeatDTO> getBookingSeatDTObyScheduleSeat(List<ScheduleSeat> scheduleSeats, Integer movieId, Integer scheduleId) {
        List<BookingSeatDTO> bookingSeatDTOS = new ArrayList<>();

        for(ScheduleSeat scheduleSeat : scheduleSeats) {
            BookingSeatDTO bookingSeatDTO = BookingSeatDTO.builder()
                    .price(scheduleSeat.getPrice())
                    .seatType(scheduleSeat.getSeat().getSeatType())
                    .seatStatus(scheduleSeat.getStatus())
                    .seatRow(scheduleSeat.getSeat().getSeatRow())
                    .seatColumn(scheduleSeat.getSeat().getSeatColumn())
                    .seatId(scheduleSeat.getSeat().getSeatId())
                    .movieId(movieId)
                    .deleted(scheduleSeat.getDeleted())
                    .scheduleId(scheduleId)
                    .scheduleSeatId(scheduleSeat.getScheduleSeatId())
                    .build();
            bookingSeatDTOS.add(bookingSeatDTO);
        }
        return bookingSeatDTOS;
    }

}
