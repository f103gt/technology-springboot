package com.technology.category.models;

import com.technology.products.models.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @Column(name = "category_name",unique = true)
    private String categoryName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category", cascade = CascadeType.MERGE)
    private Set<Product> products;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "parentCategory")
    private Set<Category> childCategories;

    /*public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private final Category category;
        private Builder(){this.category = new Category();}
        public  Builder setParentCategory(Category parentCategory){
            category.parentCategory = parentCategory;
            return this;
        }
        public Builder setCategoryName(String categoryName){
            category.categoryName = categoryName;
            return this;
        }
        public Builder setProducts(Set<Product> products){
            category.products = products;
            return this;
        }
        public Category build(){
            return category;
        }
    }*/
}

