package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "PROMOTION", schema = "MOVIETHEATER")
@Checks(@Check(constraints = "START_TIME < END_TIME"))
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMOTION_ID")
    private Integer promotionId;

    @Column(name = "DETAIL", nullable = false)
    @Nationalized
    private String detail;

    @Column(name = "DISCOUNT_LEVEL", nullable = false)
    private Integer discountLevel;

    @Version
    @Column(name = "END_TIME", nullable = false)
    private LocalDateTime endTime;



    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "TITLE", nullable = false)
    @Nationalized
    private String title;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @Column(name = "CODE")
    @Nationalized
    private String code;


    @OneToMany(mappedBy = "promotion")
    private List<Invoice> invoices;
}