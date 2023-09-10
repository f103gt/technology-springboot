package com.technology.products.models;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
public class Image{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name="image_data",nullable = false)
    private byte[] imageData;


    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "product_name",referencedColumnName = "product_name",nullable = false)
    private Product product;
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
