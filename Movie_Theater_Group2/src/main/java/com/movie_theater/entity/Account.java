package com.movie_theater.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "ACCOUNT", schema = "MOVIETHEATER")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Integer accountId;

    @Column(name = "ADDRESS", nullable = false)
    @Nationalized
    private String address;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "EMAIL", nullable = false, unique = true)
    @Nationalized
    private String email;

    @Column(name = "FULL_NAME", nullable = false)
    @Nationalized
    private String fullName;

    @Column(name = "GENDER", nullable = false)
    @Nationalized
    private String gender;

    @Column(name = "IMAGE")
    @Nationalized
    private String image;

    @Column(name = "PASSWORD", nullable = false)
    @Nationalized
    private String password;

    @Column(name = "PHONE_NUMBER", nullable = false)
    @Nationalized
    private String phoneNumber;

    @Column(name = "REGISTER_DATE", nullable = false)
    private LocalDate registerDate;

    @Column(name = "USERNAME", nullable = false, unique = true)
    @Nationalized
    private String username;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @JoinColumn(name = "ROLE_ID", foreignKey = @ForeignKey(name = "FK_ACCOUNT_ROLE"))
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Role role;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    @Column(name = "SCORE", nullable = false)
    @Nationalized
    private Integer score;
}
