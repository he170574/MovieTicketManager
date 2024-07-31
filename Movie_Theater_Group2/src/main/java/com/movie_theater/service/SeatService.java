package com.movie_theater.service;

import com.movie_theater.dto.BookingSeatDTO;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.ScheduleSeat;
import com.movie_theater.entity.Seat;
import org.hibernate.sql.Update;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SeatService {
    Seat save(Seat seat);
    List<Seat> getSeatByCinemaRoom(CinemaRoom cinemaRoom);
    List<Seat> pagingSeatByDeletedIsFalse(int pageNumber, int pageSize);
    List<Seat> getSeatByDeletedIsFalseAndCinemaRoom(CinemaRoom cinemaRoom);

    int updateDeletedBySeatId(boolean deleted, int seatId);
    int updateDeletedBySeatRowAndSeatColumnAndCinemaRoom(boolean deleted,
                                                     int seatRow,
                                                     String seatColumn,
                                                     CinemaRoom cinemaRoom
                                                     );

    int updateSeatType(int seatType, int seatId);

    Seat getSeatBySeatId(int seatId);

    List<BookingSeatDTO> getBookingSeatDTObyScheduleSeat(List<ScheduleSeat> scheduleSeats,Integer movieId, Integer scheduleId);
}
