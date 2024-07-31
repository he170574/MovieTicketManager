package com.movie_theater.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingListDTO {
    private Integer invoiceId;
    private String fullName;
    private String phoneNumber;
    private String movieName;
    private LocalDateTime scheduleTime; // Changed from String to LocalDateTime
    private String seat; // Kept as Integer if that's correct
    private Boolean status; // Added this field as it was mentioned in the error message
    private Integer invoiceItemId;
    private String cinemaRoomName;

    public BookingListDTO(Integer invoiceId, String fullName, String phoneNumber, String movieName, LocalDateTime scheduleTime, String seat) {
        this.invoiceId = invoiceId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.movieName = movieName;
        this.scheduleTime = scheduleTime;
        this.seat = seat;
    }
}
