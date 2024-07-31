package com.movie_theater.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "SCHEDULE_SEAT_HIS", schema = "MOVIETHEATER")
public class ScheduleSeatHis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_SEAT_HIS_ID")
    private Integer scheduleSeatHisId;

    @Column(name = "MOVIE_NAME", nullable = false)
    @Nationalized
    private String movieName;

    @Column(name = "SEAT_COLUMN", nullable = false)
    @Nationalized
    private String seatColumn;

    @Min(0)
    @Column(name = "SEAT_ROW", nullable = false)
    private Integer seatRow;

    @Column(name = "SEAT_TYPE", nullable = false)
    private String seatType;

    @Min(0)
    @Column(name = "PRICE", nullable = false)
    private Double price;

    @CurrentTimestamp
    @Column(name = "SCHEDULE_TIME", nullable = false)
    private LocalDateTime scheduleTime;

    @OneToMany(mappedBy = "scheduleSeatHis", fetch = FetchType.LAZY)
    private List<InvoiceItem> invoiceItems;

    @Column(name = "CINEMA_ROOM_ID")
    private int cinemaRoomId;

    @Column(name = "CINEMA_ROOM_NAME")
    private String cinemaRoomName;

    @Column(name = "MOVIE_ID")
    private int movieId;

    @Column(name = "SCHEDULE_SEAT_ID")
    private Integer scheduleSeatId;
}
