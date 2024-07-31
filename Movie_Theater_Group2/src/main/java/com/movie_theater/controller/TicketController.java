package com.movie_theater.controller;

import com.movie_theater.dto.*;
import com.movie_theater.entity.*;

import com.movie_theater.entity.key.KeyMovieSchedule;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class TicketController {

    InvoiceItemService invoiceItemService;
    AccountService accountService;
    MovieService movieService;
    MovieScheduleService movieScheduleService;
    ScheduleSeatService scheduleSeatService;
    SeatService seatService;
    InvoiceService invoiceService;
    ScheduleService scheduleService;
    @Autowired
    public TicketController(InvoiceItemService invoiceItemService, InvoiceService invoiceService ,AccountService accountService, MovieService movieService, MovieScheduleService movieScheduleService, ScheduleSeatService scheduleSeatService, SeatService seatService,ScheduleService scheduleService) {
        this.invoiceItemService = invoiceItemService;
        this.accountService = accountService;
        this.movieService = movieService;
        this.movieScheduleService = movieScheduleService;
        this.scheduleSeatService = scheduleSeatService;
        this.seatService = seatService;
        this.scheduleService = scheduleService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/get-booked-ticket")
    public ResponseEntity<ResponseDTO> getBookedTicket(@RequestParam Integer pageNumber,
                                                       @RequestParam Integer pageSize,
                                                       @RequestParam String searchByMovieName,
                                                       Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login Before").build());
        }
        Account account = accountService.getByUsername(((CustomAccount) authentication.getPrincipal()).getUsername());

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<InvoiceItemDTO> invoicePage = invoiceItemService.findInvoiceItemByAccount(account, searchByMovieName, pageable);

        List<InvoiceItemDTO> lstBookedTicketDTO = invoicePage.getContent();

        HashMap<String, Object> mapBookedTicketDTO = new HashMap<>();
        mapBookedTicketDTO.put("lstBookedTicket", lstBookedTicketDTO);
        mapBookedTicketDTO.put("pageNumber", invoicePage.getNumber());
        mapBookedTicketDTO.put("pageSize", invoicePage.getSize());
        mapBookedTicketDTO.put("totalPage", invoicePage.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder().data(mapBookedTicketDTO).build());
    }

    @GetMapping("/admin/get-booking-ticket")
    public ResponseEntity<ResponseDTO> getBookingList(@RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam(required = false) String searchByInvoiceItemId,
                                                      @RequestParam(required = false) Integer roomId,
                                                      @RequestParam(required = false) Boolean ticketStatus
    ) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Integer invoiceId = null;
        try {
            invoiceId = Integer.parseInt(searchByInvoiceItemId);
        } catch (NumberFormatException e) {
        }
        Page<InvoiceItem> pageInvoiceItem = invoiceItemService.findAllInvoice(invoiceId, roomId, ticketStatus, pageable);
        List<InvoiceItem> lstInvoiceItem = pageInvoiceItem.stream().toList();

        List<BookingListDTO> lstBookingListDTO = new ArrayList<>();
        for (InvoiceItem ii : lstInvoiceItem){
            ScheduleSeatHis scheduleSeatHis = ii.getScheduleSeatHis();
            BookingListDTO bookingListDTO = BookingListDTO.builder()
                    .movieName(scheduleSeatHis.getMovieName())
                    .scheduleTime(scheduleSeatHis.getScheduleTime())
                    .seat(scheduleSeatHis.getSeatColumn() + "-" + scheduleSeatHis.getSeatRow())
                    .status(ii.getStatus())
                    .invoiceItemId(ii.getInvoiceItemId())
                    .cinemaRoomName(scheduleSeatHis.getCinemaRoomName())
                    .build();
            lstBookingListDTO.add(bookingListDTO);
        }

        if (ticketStatus != null) {
            lstBookingListDTO = lstBookingListDTO.stream().filter(e -> e.getStatus().equals(ticketStatus)).toList();
        }

        HashMap<String, Object> mapBookListDTo = new HashMap<>();

        mapBookListDTo.put("lstBookingTicket", lstBookingListDTO);
        mapBookListDTo.put("pageNumber", pageInvoiceItem.getNumber());
        mapBookListDTo.put("pageSize", pageInvoiceItem.getSize());
        mapBookListDTo.put("totalPage", pageInvoiceItem.getTotalPages());
        return ResponseEntity.ok().body(ResponseDTO.builder().data(mapBookListDTo).build());
    }


    @PostMapping("/admin/update-status")
    public ResponseEntity<String> updateInvoiceStatusByInvoiceItemId(@RequestParam("invoiceItemId") Integer invoiceItemId,
                                                                     @RequestParam("status") Boolean status) {
        try {
            InvoiceItem invoiceItem = invoiceItemService.getById(invoiceItemId);
            if(invoiceItem == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found Ticket Id");
            }
            invoiceItem.setStatus(status);
            invoiceItemService.save(invoiceItem);
            return ResponseEntity.ok().body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status: " + e.getMessage());
        }
    }

    @GetMapping("/get-movie-schedule-by-movie-id-for-booking-ticket")
    public ResponseEntity<ResponseDTO> getMovieScheduleByMovieIdForBookingTicket(@RequestParam Integer movieId) {
        Movie movie = movieService.getById(movieId).isEmpty() ? null : movieService.getById(movieId).get();
        if (movie == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Doesn't not exist movie id").build());
        }

        List<MovieSchedule> movieSchedules = movieScheduleService.getByMovie(movie);

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> scheduleList = movieSchedules.stream()
                .map(MovieSchedule::getSchedule)
                .filter(schedule -> schedule.getScheduleTime() != null && schedule.getScheduleTime().isAfter(now) || schedule.getScheduleTime().isEqual(now))
                .toList();

        List<ScheduleDTO> scheduleDTOList = scheduleList.stream().map(item -> ScheduleDTO.builder()
                .scheduleId(item.getScheduleId())
                .scheduleTime(item.getScheduleTime())
                .deleted(item.getDeleted())
                .build()).toList();
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(scheduleDTOList).build());
    }
    @GetMapping("/get-seat-for-booking-ticket")
    public ResponseEntity<ResponseDTO> getScheduleSeatByMovieIdForBookingTicket(@RequestParam Integer movieId, @RequestParam LocalDateTime scheduleTime) {
        Optional<Schedule> scheduleOptional = scheduleService.getScheduleByTime(scheduleTime);
        Schedule schedule ;
        if(scheduleOptional.isPresent()){
            schedule = scheduleOptional.get();
        }else {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Not Found Schedule").build());
        }
        KeyMovieSchedule keyMovieSchedule = new KeyMovieSchedule(movieId, schedule.getScheduleId());
        MovieSchedule movieSchedule = MovieSchedule.builder().keyMovieSchedule(keyMovieSchedule).build();
        List<ScheduleSeat> scheduleSeats = scheduleSeatService.getByMovieSchedule(movieSchedule);

        List<BookingSeatDTO> bookingSeatDTOS = seatService.getBookingSeatDTObyScheduleSeat(scheduleSeats, movieId, schedule.getScheduleId());

        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(bookingSeatDTOS).build());
    }

    @GetMapping("/get-ticket-statistic")
    public ResponseEntity<ResponseDTO> getTicketStatistic(@RequestParam String from, @RequestParam String to) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        var data = invoiceService.findTicketCountPerDay(fromDate,toDate);
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(data).build());
    }

}