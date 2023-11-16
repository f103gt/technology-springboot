package com.technology.validation.otp.models;

import com.technology.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    private String otp;
    @Column(name = "issued_time")
    private LocalDateTime issuedTime;
    @Column(name="expiration_time")
    private LocalDateTime expirationTime;
    private boolean expired;
    private boolean revoked;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
