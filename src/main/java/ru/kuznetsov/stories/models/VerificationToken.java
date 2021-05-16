package ru.kuznetsov.stories.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "token")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "expiry_date")
    private Date expiryDate;
}
