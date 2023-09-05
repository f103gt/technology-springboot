package com.technology.products.models.products;

import com.technology.category.models.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Entity
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "category_name",referencedColumnName = "category_name", unique = true)
    private Category category;
    @Column(name = "product_name",unique = true)
    private String productName;

    @Column(unique = true)
    private String sku;
    private Integer quantity;
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST,fetch = FetchType.EAGER)
    private List<Image> images;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

