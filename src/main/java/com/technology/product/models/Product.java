package com.technology.product.models;

import com.technology.cart.models.CartItem;
import com.technology.category.models.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "product_name")
    private String productName;

    //@Column(unique = true)
    private String sku;

    private Integer quantity;

    private BigDecimal price;

    @Column(name = "description_url")
    private String descriptionUrl;

    @Column(name = "primary_image_url")
    private String primaryImageUrl;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns =
    @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<CartItem> cartItems;
}

