package com.movie_theater.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "INVOICE_ITEM", schema = "MOVIETHEATER")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_ITEM_ID")
    private Integer invoiceItemId;

    @Column(name = "STATUS", nullable = false)
    private Boolean status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", foreignKey = @ForeignKey(name = "FK_INVOICE_ITEM_INVOICE"))
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_SEAT_HIS_ID", foreignKey = @ForeignKey(name = "FK_INVOICE_ITEM_SCHEDULE_SEAT_HIS"))
    private ScheduleSeatHis scheduleSeatHis;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOOD_HIS_ID",foreignKey = @ForeignKey(name = "FK_INVOICE_ITEM_FOOD_HIS"))
    private FoodHis foodHis;
}
