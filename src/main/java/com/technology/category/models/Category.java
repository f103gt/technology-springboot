package com.technology.category.models;

import com.technology.product.models.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @Column(name = "category_name",unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Product> products;

    @OneToMany(mappedBy = "parentCategory",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Category> childCategories;
}
