package com.technology.product.images.models;

import com.technology.product.models.Product;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name="image")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name="image_data")
    private byte[] imageData;

    @Column(name = "is_primary")
    private Boolean primaryImage;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
}
