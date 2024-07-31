package com.movie_theater.controller;

import com.movie_theater.dto.MovieScheduleRoomDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.dto.ScheduleDTO;
import com.movie_theater.entity.*;
import com.movie_theater.entity.key.KeyMovieSchedule;
import com.movie_theater.final_attribute.SEAT_TYPE;
import com.movie_theater.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ScheduleController {
    private MovieService movieService;
    private ScheduleService scheduleService;
    private MovieScheduleService movieScheduleService;
    private ScheduleSeatService scheduleSeatService;
    private CinemaRoomService cinemaRoomService;
    private SeatService seatService;

    private ScheduleSeatHisService scheduleSeatHisService;

    @Autowired
    public ScheduleController(MovieService movieService
            , ScheduleService scheduleService
            , MovieScheduleService movieScheduleService
            , ScheduleSeatService scheduleSeatService
            , CinemaRoomService cinemaRoomService
            , SeatService seatService
            , ScheduleSeatHisService scheduleSeatHisService) {
        this.movieService = movieService;
        this.scheduleService = scheduleService;
        this.movieScheduleService = movieScheduleService;
        this.scheduleSeatService = scheduleSeatService;
        this.cinemaRoomService = cinemaRoomService;
        this.seatService = seatService;
        this.scheduleSeatHisService = scheduleSeatHisService;
    }

    @GetMapping("/get-all-schedule")
    private ResponseEntity<ResponseDTO> getAllSchedule() {
        ResponseDTO responseDTO = new ResponseDTO();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (Schedule schedule : scheduleService.getAll()) {
            scheduleDTOS.add(scheduleService.parseScheduleToDto(schedule));
        }
        responseDTO.setData(scheduleDTOS);
        responseDTO.setMessage("Get all schedule successful");
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/get-schedule-by-id")
    private ResponseEntity<ResponseDTO> getScheduleById(@RequestParam Integer id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Schedule> schedule = scheduleService.getOne(id);
        if (schedule.isPresent()) {
            responseDTO.setData(scheduleService.parseScheduleToDto(schedule.get()));
            responseDTO.setMessage("Get schedule by id successful");
        } else {
            responseDTO.setMessage("Schedule not found");
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/get-movie-schedule-room-by-room-id-and-day")
    private ResponseEntity<ResponseDTO> getScheduleByDayAndRoomId(@RequestParam String roomId, @RequestParam String date) {
        ResponseDTO responseDTO = new ResponseDTO();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<MovieScheduleRoomDTO> movieScheduleRoomDTOS = scheduleSeatService
                .getAllMovieScheduleRoomDTOByRoomIdAndDate(Integer.parseInt(roomId), LocalDate.parse(date, formatter));
        if(!movieScheduleRoomDTOS.isEmpty()) {
            movieScheduleRoomDTOS.forEach(item -> {
                Integer price = scheduleSeatService.getPriceBySeatTypeAndScheduleIdAndMovieId(SEAT_TYPE.VIP, item.getMovieId(), item.getScheduleId());
                item.setPriceVipSeat(price != null ? price : 0);
            });
            movieScheduleRoomDTOS.forEach(item -> {
                Integer price = scheduleSeatService.getPriceBySeatTypeAndScheduleIdAndMovieId(SEAT_TYPE.NORMAL, item.getMovieId(), item.getScheduleId());
                item.setPriceNormalSeat(price != null ? price : 0);
            });
        }

        responseDTO.setData(movieScheduleRoomDTOS);
        responseDTO.setMessage("Get list success");
        return ResponseEntity.ok().body(responseDTO);
    }
    @GetMapping("/get-movie-schedule-room-by-room-id-and-schedule")
    private ResponseEntity<ResponseDTO> getScheduleByDayAndRoomIdAndSchedule(@RequestParam Integer roomId, @RequestParam Integer scheduleId) {
        ResponseDTO responseDTO = new ResponseDTO();

        List<MovieScheduleRoomDTO> movieScheduleRoomDTOS = scheduleSeatService
                .getMovieScheduleByRoomIdAndScheduleId(roomId,scheduleId);
        if(!movieScheduleRoomDTOS.isEmpty()) {
            movieScheduleRoomDTOS.forEach(item -> {
                Integer price = scheduleSeatService.getPriceBySeatTypeAndScheduleIdAndMovieId(SEAT_TYPE.VIP, item.getMovieId(), item.getScheduleId());
                item.setPriceVipSeat(price != null ? price : 0);
            });
            movieScheduleRoomDTOS.forEach(item -> {
                Integer price = scheduleSeatService.getPriceBySeatTypeAndScheduleIdAndMovieId(SEAT_TYPE.NORMAL, item.getMovieId(), item.getScheduleId());
                item.setPriceNormalSeat(price != null ? price : 0);
            });
        }

        responseDTO.setData(movieScheduleRoomDTOS);
        responseDTO.setMessage("Get list success");
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/admin/add-new-movie-schedule-room")
    private ResponseEntity<ResponseDTO> addNewMovieScheduleRoom(@RequestBody MovieScheduleRoomDTO movieScheduleRoomDTO) {
        Optional<Movie> movieOptional = movieService.getById(movieScheduleRoomDTO.getMovieId());
        Optional<Schedule> scheduleOptional = scheduleService.getScheduleByTime(movieScheduleRoomDTO.getScheduleTime());
        Optional<CinemaRoom> cinemaRoomOptional = cinemaRoomService.getCinemaRoomByCinemaRoomIdAndDeletedIsFalse(movieScheduleRoomDTO.getCinemaRoomId());

        Schedule schedule;
        Movie movie;
        CinemaRoom cinemaRoom;
        schedule = scheduleOptional.orElseGet(() -> scheduleService.save(Schedule.builder()
                .scheduleTime(movieScheduleRoomDTO.getScheduleTime())
                .deleted(false)
                .build()));

        if(movieOptional.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie not found").build());
        }else{
            movie = movieOptional.get();
        }

        if (cinemaRoomOptional.isPresent()) {
            cinemaRoom = cinemaRoomOptional.get();
        }
        else {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Cinema Room not found").build());
        }

        // Check if this room has schedule
        List<MovieSchedule> cinemaRoomMovieSchedule = movieScheduleService.getByCinemaRoom(cinemaRoom);
        LocalDateTime newMovieScheduleStartTime = schedule.getScheduleTime();
        Integer newMovieScheduleDuration = movie.getDuration();
        LocalDateTime newMovieScheduleEndTime = newMovieScheduleStartTime.plusMinutes(newMovieScheduleDuration);

        for(MovieSchedule ms : cinemaRoomMovieSchedule){
            LocalDateTime startTime = ms.getSchedule().getScheduleTime();
            Integer duration = ms.getMovie().getDuration();
            LocalDateTime endTime = startTime.plusMinutes(duration);

            if(     (newMovieScheduleStartTime.isAfter(startTime) && newMovieScheduleStartTime.isBefore(endTime))   ||
                    (newMovieScheduleEndTime.isAfter(startTime) && newMovieScheduleEndTime.isBefore(endTime))       ||
                    (newMovieScheduleStartTime.isBefore(startTime) && newMovieScheduleEndTime.isAfter(endTime))     ||
                    (newMovieScheduleStartTime.isAfter(startTime) && newMovieScheduleEndTime.isBefore(endTime))     ||
                    newMovieScheduleStartTime.equals(startTime)
            ){
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie showtime have clashed").build());
            }
        }

        //save
        MovieSchedule movieSchedule = MovieSchedule.builder()
                .keyMovieSchedule(new KeyMovieSchedule(movie.getMovieId(), schedule.getScheduleId()))
                .deleted(false)
                .movie(movie)
                .schedule(schedule)
                .build();

        //Save Movie Schedule
        movieSchedule = movieScheduleService.save(movieSchedule);


        //Create Ticket
            List<Seat> seatList = cinemaRoom.getSeats();

            for (Seat seat : seatList) {
                ScheduleSeat scheduleSeat = ScheduleSeat.builder()
                        .seat(seat)
                        .movieSchedule(movieSchedule)
                        .status(false)
                        .deleted(seat.getDeleted())
                        .price(seat.getSeatType() == SEAT_TYPE.NORMAL ? movieScheduleRoomDTO.getPriceNormalSeat() : movieScheduleRoomDTO.getPriceVipSeat())
                        .build();
                scheduleSeatService.save(scheduleSeat);
            }
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").build());

    }

    @PostMapping("/admin/update-movie-schedule")
    private ResponseEntity<ResponseDTO> updateMovieSchedule(@RequestBody MovieScheduleRoomDTO movieScheduleRoomDTO){

        Optional<Movie> movieOptional = movieService.getById(movieScheduleRoomDTO.getMovieId());
        Optional<Schedule> scheduleOptional = scheduleService.getScheduleByTime(movieScheduleRoomDTO.getScheduleTime());
        Optional<CinemaRoom> cinemaRoomOptional = cinemaRoomService.getCinemaRoomByCinemaRoomIdAndDeletedIsFalse(movieScheduleRoomDTO.getCinemaRoomId());
        Optional<MovieSchedule> movieScheduleOldOptional = movieScheduleService.getById(new KeyMovieSchedule(movieScheduleRoomDTO.getMovieIdOld(), movieScheduleRoomDTO.getScheduleIdOld()));

        Schedule schedule;
        Movie movie;
        CinemaRoom cinemaRoom;
        MovieSchedule movieScheduleOld;

        schedule = scheduleOptional.orElseGet(() -> scheduleService.save(Schedule.builder()
                .scheduleTime(movieScheduleRoomDTO.getScheduleTime())
                .deleted(false)
                .build()));

        if(movieOptional.isPresent()){
            movie = movieOptional.get();
        }else{
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie not found").build());
        }

        if (cinemaRoomOptional.isPresent()) {
            cinemaRoom = cinemaRoomOptional.get();
        }
        else {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Cinema Room not found").build());
        }

        if(movieScheduleOldOptional.isPresent()){
            movieScheduleOld = movieScheduleOldOptional.get();
        }else{
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie Schedule not found").build());
        }

        //Check If User Booked Ticket
        List<ScheduleSeatHis> lstScheduleSeatBooked = scheduleSeatHisService.getByCinemaRoomIdAndMovieIdAndScheduleTime(cinemaRoom.getCinemaRoomId(),movie.getMovieId(),schedule.getScheduleTime());


       if(!lstScheduleSeatBooked.isEmpty()){
           return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie tickets have been purchased, the schedule cannot be edited, please contact the customer and delete the tickets in the ticket management section").build());
       }

        // Check if this room has schedule
        List<MovieSchedule> cinemaRoomMovieSchedule = movieScheduleService.getByCinemaRoom(cinemaRoom);
        LocalDateTime newMovieScheduleStartTime = schedule.getScheduleTime();
        Integer newMovieScheduleDuration = movie.getDuration();
        LocalDateTime newMovieScheduleEndTime = newMovieScheduleStartTime.plusMinutes(newMovieScheduleDuration);

        for(MovieSchedule ms : cinemaRoomMovieSchedule){
            if(ms.getKeyMovieSchedule().getScheduleId().equals(movieScheduleOld.getSchedule().getScheduleId())  && ms.getKeyMovieSchedule().getMovieId().equals(movieScheduleOld.getKeyMovieSchedule().getMovieId())){
                continue;
            }

            LocalDateTime startTime = ms.getSchedule().getScheduleTime();
            Integer duration = ms.getMovie().getDuration();
            LocalDateTime endTime = startTime.plusMinutes(duration);

            if(     (newMovieScheduleStartTime.isAfter(startTime) && newMovieScheduleStartTime.isBefore(endTime))   ||
                    (newMovieScheduleEndTime.isAfter(startTime) && newMovieScheduleEndTime.isBefore(endTime))       ||
                    (newMovieScheduleStartTime.isBefore(startTime) && newMovieScheduleEndTime.isAfter(endTime))     ||
                    (newMovieScheduleStartTime.isAfter(startTime) && newMovieScheduleEndTime.isBefore(endTime))     ||
                    newMovieScheduleStartTime.equals(startTime)


            ){
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie showtime have clashed").build());
            }
        }


        //delete before
        scheduleSeatService.deleteByMovieSchedule(movieScheduleOld);
        movieScheduleService.delete(movieScheduleOld);

        //save
        MovieSchedule movieSchedule = MovieSchedule.builder()
                .keyMovieSchedule(new KeyMovieSchedule(movie.getMovieId(), schedule.getScheduleId()))
                .deleted(false)
                .movie(movie)
                .schedule(schedule)
                .build();

        //Save Movie Schedule
        movieSchedule = movieScheduleService.save(movieSchedule);


        //Create Ticket
        List<Seat> seatList = cinemaRoom.getSeats();

        for (Seat seat : seatList) {
            ScheduleSeat scheduleSeat = ScheduleSeat.builder()
                    .seat(seat)
                    .movieSchedule(movieSchedule)
                    .status(false)
                    .deleted(false)
                    .price(seat.getSeatType() == SEAT_TYPE.NORMAL ? movieScheduleRoomDTO.getPriceNormalSeat() : movieScheduleRoomDTO.getPriceVipSeat())
                    .build();
            scheduleSeatService.save(scheduleSeat);
        }
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").build());
    }

    @PostMapping("/admin/delete-movie-schedule")
    private ResponseEntity<ResponseDTO> deleteMovieSchedule(@RequestBody MovieScheduleRoomDTO movieScheduleRoomDTO){
        List<ScheduleSeatHis> lstScheduleSeatBooked = scheduleSeatHisService.getByCinemaRoomIdAndMovieIdAndScheduleTime(movieScheduleRoomDTO.getCinemaRoomId(),movieScheduleRoomDTO.getMovieId(),movieScheduleRoomDTO.getScheduleTime());


        if(!lstScheduleSeatBooked.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Movie tickets have been purchased, the schedule cannot be edited, please contact the customer and delete the tickets in the ticket management section").build());
        }

        Optional<MovieSchedule> movieScheduleOptional = movieScheduleService.getById(new KeyMovieSchedule(movieScheduleRoomDTO.getMovieId(),movieScheduleRoomDTO.getScheduleId()));
        MovieSchedule movieSchedule;
        if(movieScheduleOptional.isPresent()){
            movieSchedule = movieScheduleOptional.get();
        }else {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Schedule not found").build());
        }
        scheduleSeatService.deleteByMovieSchedule(movieSchedule);
        movieScheduleService.delete(movieSchedule);

        return ResponseEntity.ok().body(ResponseDTO.builder().build());
    }
}