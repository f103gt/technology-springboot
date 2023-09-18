package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.models.Product;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWithNoChildCategoriesRepositoryTest extends CategoryRepositoryTest{
    public CategoryWithNoChildCategoriesRepositoryTest(CategoryRepository categoryRepository, ProductRepository productRepository) {
        super(categoryRepository, productRepository);
    }
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1)
                .categoryName("Test Category")
                .build();
        categoryRepository.save(category);
    }

    @Test
    void deleteCategory_DeletesCategory_CategoryWithNoChildCategories() {
        //given
        String categoryName = category.getCategoryName();

        Product product =
                Product.builder()
                        .category(category)
                        .productName("Test Product 1")
                        .sku("SKU1")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build();

        productRepository.save(product);
        category.setProducts(Set.of(product));
        categoryRepository.save(category);
        //when
        categoryRepository.delete(category);

        //then
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(categoryName);

        assertThat(deletedCategory).isEmpty();
        Set<Product> deletedProducts =
                productRepository.findProductsByCategoryId(category.getId());
        assertThat(deletedProducts).isEmpty();
    }
}
