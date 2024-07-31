package com.movie_theater.service;

import com.movie_theater.dto.BookingListDTO;

import java.util.List;

public interface BookingListService {
    List<BookingListDTO> findActiveBookings();

    List<BookingListDTO> findByInvoiceID(String idSearch);
}
