package com.movie_theater.service.impl;

import com.movie_theater.dto.BookingListDTO;
import com.movie_theater.repository.BookingListRepository;
import com.movie_theater.service.BookingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingListServiceImpl implements BookingListService {

    private BookingListRepository bookingListRepository;

    @Autowired
    public BookingListServiceImpl(BookingListRepository bookingListRepository) {
        this.bookingListRepository = bookingListRepository;
    }

    @Override
    @Transactional
    public List<BookingListDTO> findActiveBookings() {
        return bookingListRepository.findActiveBookings();
    }

    @Override
    @Transactional
    public List<BookingListDTO> findByInvoiceID(String idSearch) {
        return bookingListRepository.findByInvoiceID(idSearch);
    }
}