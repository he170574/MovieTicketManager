package com.movie_theater.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "INVOICE", schema = "MOVIETHEATER")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_ID")
    private Integer invoiceId;

    @Min(0)
    @Column(name = "ADD_SCORE", nullable = false)
    private Integer addScore;

    @Column(name = "BOOKING_DATE", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "PROMOTION_ID",referencedColumnName = "PROMOTION_ID")
    private Promotion promotion;

    @Min(0)
    @Column(name = "USE_SCORE", nullable = false)
    private Integer useScore;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", foreignKey = @ForeignKey(name = "FK_INVOICE_ACCOUNT"))
    private Account account;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<InvoiceItem> invoiceItem;

}
