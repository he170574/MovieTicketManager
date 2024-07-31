package com.movie_theater.dto.jsonDTO;

import lombok.Getter;
import lombok.Setter;

@lombok.Data
@Getter
@Setter
public class TransactionDTO {
    public int error;
    public String message;
    public Data data;
}
