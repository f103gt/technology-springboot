package com.technology.product.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Entity
public class Image{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name="image_data",nullable = false)
    private byte[] imageData;


    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
