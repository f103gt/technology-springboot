package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.models.Product;
import com.technology.products.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {
    protected final CategoryRepository categoryRepository;
    protected final ProductRepository productRepository;
    protected Category parentCategory;
    protected Category childCategoryFirst;

    protected Category childCategorySecond;

    protected String parentCategoryName;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository categoryRepository,
                                  ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @BeforeEach
    void setUp() {
        parentCategoryName = "Parent Category";
        parentCategory = Category.builder()
                .id(1)
                .categoryName(parentCategoryName)
                .build();
        categoryRepository.save(parentCategory);
    }

    protected void createChildCategories() {
        childCategoryFirst = Category.builder()
                .id(2)
                .categoryName("Child Category 1")
                .parentCategory(parentCategory)
                .build();

        childCategorySecond = Category.builder()
                .id(3)
                .categoryName("Child Category 2")
                .parentCategory(parentCategory)
                .build();

        categoryRepository.save(childCategoryFirst);
        categoryRepository.save(childCategorySecond);

        parentCategory.setChildCategories(Set.of(childCategoryFirst, childCategorySecond));
        categoryRepository.save(parentCategory);
    }

    protected void createProductsForChildCategories() {
        List<Category> childCategories =
                parentCategory.getChildCategories().stream().toList();
        Product productFirst = Product.builder()
                .id(BigInteger.ONE)
                .category(childCategoryFirst)
                .productName("Test Product 1")
                .sku("SKU1")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Product productSecond = Product.builder()
                .id(BigInteger.TWO)
                .category(childCategorySecond)
                .productName("Test Product 2")
                .sku("SKU2")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();
        productRepository.save(productFirst);

        childCategoryFirst.setProducts(Set.of(productFirst));
        categoryRepository.save(childCategoryFirst);

        productRepository.save(productSecond);

        childCategorySecond.setProducts(Set.of(productSecond));
        categoryRepository.save(childCategorySecond);
    }
}