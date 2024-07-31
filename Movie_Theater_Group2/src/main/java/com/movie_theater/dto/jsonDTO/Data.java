package com.movie_theater.dto.jsonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@lombok.Data
@Getter
@Setter
public class Data {
    public int page;
    public int pageSize;
    public int nextPage;
    public int prevPage;
    public int totalPages;
    public int totalRecords;
    public List<Record> records;
}
