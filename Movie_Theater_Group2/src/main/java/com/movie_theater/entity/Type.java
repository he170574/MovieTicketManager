package com.movie_theater.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "TYPE", schema = "MOVIETHEATER")
public class Type {
    @Id
    @Column(name = "TYPE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer typeId;

    @Nationalized
    @Column(name = "TYPE_NAME", nullable = false, unique = true)
    private String typeName;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @JsonManagedReference
    @ToString.Exclude
    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private List<TypeMovie> typeMovies = new ArrayList<>();
}
