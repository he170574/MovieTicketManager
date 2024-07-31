package com.movie_theater.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "COMMENT", schema = "MOVIETHEATER")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATED_TIME")
    private LocalTime createdTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ACCOUNT_ID")
    private Account author;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_ID", referencedColumnName = "ID")
    private Comment reply;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOVIE_ID", referencedColumnName = "MOVIE_ID")
    private Movie movie;
}
