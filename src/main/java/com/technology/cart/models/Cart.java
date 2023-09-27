package com.technology.cart.models;

import com.technology.user.registration.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @OneToOne
    @JoinColumn(name = "client_id",referencedColumnName = "id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,mappedBy = "cart")
    Collection<CartItem> cartItems;
}
