package com.technology.cart.models;

import com.technology.product.models.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name="cart_id")
    private Cart cart;

    private Integer quantity;

    @Column(name = "price")
    private BigDecimal finalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
