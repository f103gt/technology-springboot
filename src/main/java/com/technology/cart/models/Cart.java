package com.technology.cart.models;

import com.technology.registration.models.User;
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
    private BigInteger cart_id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE,mappedBy = "cart")
    Collection<CartItem> cartItems;
}
