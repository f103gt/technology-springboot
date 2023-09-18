package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWithChildCategoriesRepositoryTest extends CategoryRepositoryTest{
    public CategoryWithChildCategoriesRepositoryTest(CategoryRepository categoryRepository, ProductRepository productRepository) {
        super(categoryRepository, productRepository);
    }

    private Category parentCategory;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .id(1)
                .categoryName("Parent Category")
                .build();
        categoryRepository.save(parentCategory);

        Category childCategoryFirst = Category.builder()
                .id(2)
                .categoryName("Child Category 1")
                .parentCategory(parentCategory)
                .build();
        categoryRepository.save(childCategoryFirst);

        Category childCategorySecond = Category.builder()
                .id(3)
                .categoryName("Child Category 2")
                .parentCategory(parentCategory)
                .build();
        categoryRepository.save(childCategorySecond);

        parentCategory.setChildCategories(Set.of(childCategoryFirst, childCategorySecond));
        categoryRepository.save(parentCategory);
    }

    @Test
    void deleteCategory_DeletesCategory_CategoryWithChildCategories_WithNoProducts() {
        //arrange
        String categoryName = parentCategory.getCategoryName();

        //act
        categoryRepository.delete(parentCategory);

        //assert
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(categoryName);

        assertThat(deletedCategory).isEmpty();

        Set<Category> deletedChildCategories =
                categoryRepository.findAllChildCategoriesByParentCategoryId(parentCategory.getId());

        assertThat(deletedChildCategories).isEmpty();

    }
}
