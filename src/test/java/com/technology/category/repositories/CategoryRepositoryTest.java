package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {
    protected final CategoryRepository categoryRepository;
    protected final ProductRepository productRepository;
    protected Category parentCategory;

    protected String parentCategoryName;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository categoryRepository,
                                  ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @BeforeEach
    void setUp(){
        parentCategoryName = "Parent Category";
        parentCategory = Category.builder()
                .id(1)
                .categoryName(parentCategoryName)
                .build();
    }
}