package com.movie_theater.controller;

import com.movie_theater.dto.*;
import com.movie_theater.entity.*;
import com.movie_theater.final_attribute.STATUS;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class ScheduleSeatController {

    MovieService movieService;
    ScheduleService scheduleService;

    MovieScheduleService movieScheduleService;

    ScheduleSeatService scheduleSeatService;

    ScheduleSeatHisService scheduleSeatHisService;

    InvoiceItemService invoiceItemService;

    InvoiceService invoiceService;

    @Autowired
    AccountService accountService;



    @Autowired
    public ScheduleSeatController(MovieService movieService, ScheduleService scheduleService, MovieScheduleService movieScheduleService, ScheduleSeatService scheduleSeatService, ScheduleSeatHisService scheduleSeatHisService, InvoiceItemService invoiceItemService, InvoiceService invoiceService) {
        this.movieService = movieService;
        this.scheduleService = scheduleService;
        this.movieScheduleService = movieScheduleService;
        this.scheduleSeatService = scheduleSeatService;
        this.scheduleSeatHisService = scheduleSeatHisService;
        this.invoiceItemService = invoiceItemService;
        this.invoiceService = invoiceService;
    }



    @GetMapping("/get-seat-by-movie-id-and-schedule")
    public ResponseEntity<ResponseDTO> getSeatByMovieIdAndSchedule(@RequestParam Integer movieId,
                                                                   @RequestParam LocalDateTime scheduleTime){

        Movie movie = movieService.getById(movieId).isEmpty() ? null : movieService.getById(movieId).get();
        Schedule schedule = scheduleService.getScheduleByTime(scheduleTime).isEmpty() ? null :scheduleService.getScheduleByTime(scheduleTime).get();

        if(movie == null ){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Doesn't Exit Movie Id").build());
        }
        if(schedule == null){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Doesn't Exit Schedule Time").build());
        }

        MovieSchedule movieSchedule = movieScheduleService.getByMovieAndSchedule(movie,schedule);

        if (movieSchedule == null){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Doesn't Exit Movie Schedule").build());
        }

        List<ScheduleSeat> scheduleSeat = scheduleSeatService.getScheduleSeatByMovieSchedule(movieSchedule);

        List<ScheduleSeatDTO> scheduleSeatDTOList = scheduleSeat.stream().map(item -> ScheduleSeatDTO.builder()
                .scheduleSeatId(item.getScheduleSeatId())
                .seatRow(item.getSeat().getSeatRow())
                .seatColumn(item.getSeat().getSeatColumn())
                .price(item.getPrice())
                .selected(item.getDeleted())
                .build()).toList();

        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(scheduleSeatDTOList).build());
    }

    @PostMapping("/book-ticket")
    public ResponseEntity<ResponseDTO> bookTicket(@RequestBody BookTicketDTO bookTicketDTO, Authentication authentication){

        List<Integer> lstSeatId = bookTicketDTO.getSeats();
        AtomicReference<Double> totalMoney = new AtomicReference<>(0d);
        if(authentication == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login Before").build());
        }

        CustomAccount customAccount = (CustomAccount)authentication.getPrincipal();
        Account account = accountService.getByUsername(customAccount.getUsername());

        List<ScheduleSeat> scheduleSeatList = lstSeatId.stream().map(item ->  scheduleSeatService.getOne(item.intValue()).isEmpty() ? null : scheduleSeatService.getOne(item.intValue()).get()).toList();


        List<ScheduleSeatHis> scheduleSeatHisList = scheduleSeatList.stream().map(item -> {
            item.setStatus(true);
            totalMoney.updateAndGet(v -> v + item.getPrice());
            return ScheduleSeatHis.builder()
                    .scheduleSeatId(item.getScheduleSeatId())
                    .price(item.getPrice())
                    .seatRow(item.getSeat().getSeatRow())
                    .scheduleTime(item.getMovieSchedule().getSchedule().getScheduleTime())
                    .movieName(item.getMovieSchedule().getMovie().getMovieName())
                    .cinemaRoomId(item.getSeat().getCinemaRoom().getCinemaRoomId())
                    .cinemaRoomName(item.getSeat().getCinemaRoom().getCinemaRoomName())
                    .seatColumn(item.getSeat().getSeatColumn())
                    .seatType(item.getSeat().getSeatType().toString())
                    .cinemaRoomName(item.getSeat().getCinemaRoom().getCinemaRoomName())
                    .build();
        }).toList();


        Invoice invoice = invoiceService.save(Invoice.builder()
                .account(account)
                .addScore((int) Math.round(totalMoney.get() * 0.0001))
                .deleted(false)
                .status(STATUS.WAITING)
                .useScore(0)
                .bookingDate(LocalDateTime.now())
                .build());

        for (ScheduleSeatHis scheduleSeatHis : scheduleSeatHisList){
            ScheduleSeatHis scheduleSeatHisSaved = scheduleSeatHisService.save(scheduleSeatHis);
            InvoiceItem invoiceItem = invoiceItemService.save(InvoiceItem.builder()
                            .scheduleSeatHis(scheduleSeatHisSaved)
                            .invoice(invoice)
                            .status(false)
                            .quantity(1)
//                            .foodHis(null)

                    .build());
        }



        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(invoice.getInvoiceId()).build());
    }
}
