package com.movie_theater.controller;

import com.movie_theater.dto.CinemaRoomDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.dto.SeatDTO;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Seat;
import com.movie_theater.service.CinemaRoomService;
import com.movie_theater.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CinemaRoomController {
    private CinemaRoomService cinemaRoomService;
    private SeatService seatService;

    @Autowired
    public CinemaRoomController(CinemaRoomService cinemaRoomService, SeatService seatService) {
        this.cinemaRoomService = cinemaRoomService;
        this.seatService = seatService;
    }
    @PostMapping("/admin/seat-detail-list")
    public ResponseEntity<ResponseDTO> loadSeatDetail(@RequestBody CinemaRoomDTO cinemaRoomDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        CinemaRoom cinemaRoom = CinemaRoom.builder()
                .cinemaRoomId(cinemaRoomDTO.getId())
                .build();
        List<Seat> seats = seatService.getSeatByCinemaRoom(cinemaRoom);
        responseDTO.setMessage("Success");
        responseDTO.setData(seats);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/admin/cinema-room-list")
    public ResponseEntity<ResponseDTO> getCinemaRoom (@RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam String searchByCinemaRoomName) {

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<CinemaRoom> cinemaRooms = cinemaRoomService.findByCinemaRoomNameContainingAndDeletedIsFalse(searchByCinemaRoomName, pageable);

        List<CinemaRoom> listCinemaRooms = cinemaRooms.getContent();

        HashMap<String, Object> mapCinemaRoom = new HashMap<>();

        mapCinemaRoom.put("listCinemaRooms", listCinemaRooms);
        mapCinemaRoom.put("pageNumber",cinemaRooms.getNumber());
        mapCinemaRoom.put("pageSize",cinemaRooms.getSize());
        mapCinemaRoom.put("totalPage",cinemaRooms.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder().data(mapCinemaRoom).build());
    }

    @PostMapping("/admin/cinema-room-list")
    public ResponseEntity<String> saveCinemaRoom(@RequestBody CinemaRoomDTO cinemaRoomDTO) {
        int seatQuantity = 0;
        List<SeatDTO> seatDTOS = cinemaRoomDTO.getSeatDTOS();

        for(SeatDTO seatDTO : seatDTOS) {
            seatQuantity += seatDTO.getSeatNumber();
        }

        CinemaRoom cinemaRoom = CinemaRoom.builder()
                .cinemaRoomName(cinemaRoomDTO.getCinemaRoomName())
                .seatQuantity(seatQuantity)
                .deleted(false)
                .build();

        CinemaRoom cinemaRoomSaved = cinemaRoomService.save(cinemaRoom);

        for (int i = 0; i < seatDTOS.size(); i++) {
            for (int j = 0; j < seatDTOS.get(i).getSeatNumber(); j++) {
                Seat seat = Seat.builder()
                        .cinemaRoom(cinemaRoomSaved)
                        .seatRow(seatDTOS.get(i).getSeatRow())
                        .seatColumn(generateAlphabetCharacter(j))
                        .deleted(false)
                        .seatType(0)
                        .build();
                seatService.save(seat);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/delete-cinema-room")
    public ResponseEntity<String> deleteCinemaRoom(@RequestBody CinemaRoomDTO cinemaRoomDTO) {
        cinemaRoomService.updateDeleted(true, cinemaRoomDTO.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/update-cinema-room-name")
    public ResponseEntity<String> updateCinemaRoomName(@RequestBody CinemaRoomDTO cinemaRoomDTO) {
        cinemaRoomService.updateCinemaRoomName(cinemaRoomDTO.getCinemaRoomName(), cinemaRoomDTO.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/get-cinema-room-name")
    public ResponseEntity<ResponseDTO> getCinemaRoomName(@RequestParam Integer cinemaRoomId) {
        CinemaRoom cinemaRoom = cinemaRoomService.getCinemaRoomByCinemaRoomId(cinemaRoomId);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(cinemaRoom.getCinemaRoomName());
        if(cinemaRoom != null) {
            responseDTO.setMessage("Success");
        } else {
            responseDTO.setMessage("Get cinema room name fail!");
        }

        return ResponseEntity.ok().body(responseDTO);
    }
    private String generateAlphabetCharacter(int i) {
        StringBuilder result = new StringBuilder();
        while (i >= 0) {
            result.insert(0, (char)('A' + i % 26));
            i = i / 26 - 1;
        }
        return result.toString();
    }
//update

    @GetMapping("/get-all-cinema-room")
    public ResponseEntity<ResponseDTO> getAllCinemaRoom() {
        ResponseDTO responseDTO = new ResponseDTO();

        List<CinemaRoom> cinemaRooms = cinemaRoomService.getCinemaRoomByDeletedIsFalse();

        List<CinemaRoomDTO> cinemaRoomDTOs = cinemaRooms.stream()
                .map(cinemaRoom -> CinemaRoomDTO.builder()
                        .id(cinemaRoom.getCinemaRoomId())
                        .cinemaRoomName(cinemaRoom.getCinemaRoomName())
                        .build())
                .toList();

        responseDTO.setData(cinemaRoomDTOs);
        responseDTO.setMessage("Get all cinema room success");

        return ResponseEntity.ok().body(responseDTO);
    }
}
