package com.movie_theater.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FOOD_HIS", schema = "MOVIETHEATER")
public class FoodHis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_HIS_ID")
    private Integer foodId;
    @Column(name = "FOOD_NAME")
    private String foodName;
    @Column(name = "FOOD_SIZE")
    private String foodSize;
    @Column(name = "FOOD_PRICE")
    private float foodPrice;
    @Column(name = "FOOD_IMAGE")
    private String foodImage;

    @OneToMany(mappedBy = "foodHis", fetch = FetchType.LAZY)
    private List<InvoiceItem> invoiceItem;
}
