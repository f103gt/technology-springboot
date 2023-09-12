package com.technology.products.models;

import com.technology.category.models.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", unique = true, nullable = false)
    private Category category;
    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;

    @Column(unique = true, nullable = false)
    private String sku;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Image> images;

}

