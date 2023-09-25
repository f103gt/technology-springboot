package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.category.test.repositories.TestProductRepository;
import com.technology.product.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWithChildCategoriesRepositoryTest extends CategoryRepositoryTest {
    @Autowired
    public CategoryWithChildCategoriesRepositoryTest(CategoryRepository categoryRepository, TestProductRepository productRepository) {
        super(categoryRepository, productRepository);
    }

    @Test
    @DirtiesContext
    void deleteCategory_DeletesCategory_CategoryWithChildCategories_WithNoProducts() {

        //arrange(setUp)
        createChildCategories(2);

        //act
        categoryRepository.delete(parentCategory);

        //assert
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(parentCategory.getCategoryName());

        assertThat(deletedCategory).isEmpty();

        Set<Category> deletedChildCategories =
                categoryRepository.findAllChildCategoriesByParentCategoryId(parentCategory.getId());

        assertThat(deletedChildCategories).isEmpty();
    }

    @Test
    @DirtiesContext
    void deleteCategory_DeletesCategory_CategoryWithChildCategories_ChildCategoriesWithProducts() {
        //arrange(setUp)
        createChildCategories(2);
        parentCategory.getChildCategories().forEach(childCategory ->
                createProductsForChildCategories(1,childCategory));

        //act
        categoryRepository.delete(parentCategory);

        //assert
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(parentCategory.getCategoryName());

        assertThat(deletedCategory).isEmpty();

        Set<Category> deletedChildCategories =
                categoryRepository.findAllChildCategoriesByParentCategoryId(parentCategory.getId());
        assertThat(deletedChildCategories).isEmpty();

        parentCategory.getChildCategories().forEach(childCategory -> {
            Set<Product> deletedProducts =
                    productRepository.findProductsByCategoryId(childCategory.getId());
            assertThat(deletedProducts).isEmpty();
        });
        /*Set<Product> deletedProductsSecondChildCategory =
                productRepository.findProductsByCategoryId(parentCategory.getChildCategories().stream().toList().get(0).getId());

        assertThat(deletedProductsSecondChildCategory).isEmpty();*/

    }
}
