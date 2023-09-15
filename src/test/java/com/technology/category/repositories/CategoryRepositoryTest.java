package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.models.Product;
import com.technology.products.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository categoryRepository,
                                  ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }
    @Test
    void deleteCategoryByCategoryName() {
        //given
        String categoryName = "Test Category";
        String productName = "Test Product 1";
        Category category = Category.builder()
                .id(1)
                .categoryName(categoryName)
                .build();
        categoryRepository.save(category);

        Product product =
                Product.builder()
                        .category(category)
                        .productName(productName)
                        .sku("SKU1")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build();

        productRepository.save(product);

        Set<Product> products = Set.of(product);
        category.setProducts(products);
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