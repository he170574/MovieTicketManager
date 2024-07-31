package com.movie_theater.dto.jsonDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Record {
    public int id;
    public String tid;
    public String description;
    public long amount;
    public String when;
    public int accountId;
    public String bankCodeName;
}
