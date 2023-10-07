package com.technology.activity.models;

import com.technology.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name="client_id")
    private User user;

    @Column(name = "is_available")
    private Boolean isAvailable;

    private BigInteger points;
}
