package com.movie_theater.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemDTO {
    private String movieName;
    private Double price;
    private Integer discount;
    private LocalDateTime scheduleTime;
    private String seat;
    private String seatType;
    private LocalDateTime bookingDate;
    private Boolean status;
    private String foodName;
    private Float foodPrice;
    private String foodSize;
    private String cinemaRoomName;

    public InvoiceItemDTO(String movieName, Double price,  LocalDateTime scheduleTime, String seat, String seatType,LocalDateTime bookingDate,Boolean status,String cinemaRoomName) {
        this.movieName = movieName;
        this.price = price;
        this.scheduleTime = scheduleTime;
        this.seat = seat;
        this.seatType = seatType;
        this.bookingDate = bookingDate;
        this.status = status;
        this.cinemaRoomName = cinemaRoomName;
    }

    public InvoiceItemDTO(String movieName, Double price,  LocalDateTime scheduleTime, String seat, String seatType,String foodName,Float foodPrice,String foodSize) {
        this.movieName = movieName;
        this.price = price;
        this.scheduleTime = scheduleTime;
        this.seat = seat;
        this.seatType = seatType;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodSize = foodSize;
    }
}
