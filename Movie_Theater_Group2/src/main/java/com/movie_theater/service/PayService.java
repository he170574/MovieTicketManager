package com.movie_theater.service;

import com.movie_theater.dto.ResponseDTO;

public interface PayService {
    boolean isTransactionCodeExisted(String transactionCode);

    boolean isTransactionCompleted(String transactionCode, long amount,String email);

    String createTransactionCode();
}
