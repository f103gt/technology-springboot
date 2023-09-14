package com.technology.category.repositories;

import com.technology.category.models.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Test
    void deleteCategoryByCategoryName() {
        //given
        Category category = Category.builder()
                .categoryName("Test Category")
                .childCategories()
                .build();

    }
}