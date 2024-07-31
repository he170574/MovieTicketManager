package com.movie_theater.controller;


import com.movie_theater.dto.SeatDTO;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Seat;
import com.movie_theater.service.CinemaRoomService;
import com.movie_theater.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SeatController {
    private CinemaRoomService cinemaRoomService;
    private SeatService seatService;

    @Autowired
    public SeatController(CinemaRoomService cinemaRoomService, SeatService seatService) {
        this.cinemaRoomService = cinemaRoomService;
        this.seatService = seatService;
    }

    @PostMapping("/admin/update-type-seat")
    public ResponseEntity<String> updateTypeSeat(@RequestBody SeatDTO seatDTO) {
        List<Integer> seatIdsBookedVip = seatDTO.getSeatIdsBookedVip();
        List<Integer> seatIdsCancelledVip = seatDTO.getSeatIdsCancelledVip();

        for (int i = 0; i < seatIdsBookedVip.size(); i++) {
            seatService.updateSeatType(1, seatIdsBookedVip.get(i));
        }

        for (int i = 0; i < seatIdsCancelledVip.size(); i++) {
            seatService.updateSeatType(0, seatIdsCancelledVip.get(i));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/update-deleted-seat")
    public ResponseEntity<String> updateDeletedSeat(@RequestBody SeatDTO seatDTO) {
        List<String> seatNamesAdded = seatDTO.getSeatNamesAdded();
        List<String> seatNamesDeleted = seatDTO.getSeatNamesDeleted();
        CinemaRoom cinemaRoom = CinemaRoom.builder().cinemaRoomId(seatDTO.getCinemaRoomId()).build();
//        int numberRowEffect = 0;
        for (int i = 0; i < seatNamesAdded.size(); i++) {
              int isUpdated = seatService.updateDeletedBySeatRowAndSeatColumnAndCinemaRoom(false,
                    Integer.parseInt(String.valueOf(seatNamesAdded.get(i).charAt(0))),
                    String.valueOf(seatNamesAdded.get(i).charAt(1)), cinemaRoom
              );

              if(isUpdated != 1) {
                  Seat seat = Seat.builder()
                          .seatType(0)
                          .deleted(false)
                          .seatColumn(String.valueOf(seatNamesAdded.get(i).charAt(1)))
                          .seatRow(Integer.parseInt(String.valueOf(seatNamesAdded.get(i).charAt(0))))
                          .cinemaRoom(cinemaRoom)
                          .build();
                  seatService.save(seat);
              }
        }

//        if (numberRowEffect == 0){
//            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Error").build());
//        }

        for (int i = 0; i < seatNamesDeleted.size(); i++) {
            int isUpdated = seatService.updateDeletedBySeatRowAndSeatColumnAndCinemaRoom(true,
                    Integer.parseInt(String.valueOf(seatNamesDeleted.get(i).charAt(0))),
                    String.valueOf(seatNamesDeleted.get(i).charAt(1)), cinemaRoom
            );

            if(isUpdated != 1) {
                Seat seat = Seat.builder()
                        .seatType(0)
                        .deleted(true)
                        .seatColumn(String.valueOf(seatNamesDeleted.get(i).charAt(1)))
                        .seatRow(Integer.parseInt(String.valueOf(seatNamesDeleted.get(i).charAt(0))))
                        .cinemaRoom(cinemaRoom)
                        .build();
                seatService.save(seat);
            }
        }

        cinemaRoomService.updateSeatQuantity(cinemaRoom);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
