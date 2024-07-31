package com.movie_theater.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayDTO {
    private String amount;
    private String transactionCode;
    private String invoiceId;
}
