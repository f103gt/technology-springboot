package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.models.Product;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWithChildCategoriesRepositoryTest extends CategoryRepositoryTest {
    @Autowired
    public CategoryWithChildCategoriesRepositoryTest(CategoryRepository categoryRepository, ProductRepository productRepository) {
        super(categoryRepository, productRepository);
    }

    @Test
    void deleteCategory_DeletesCategory_CategoryWithChildCategories_WithNoProducts() {

        //arrange(setUp)
        createChildCategories();

        //act
        categoryRepository.delete(parentCategory);

        //assert
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(parentCategoryName);

        assertThat(deletedCategory).isEmpty();

        Set<Category> deletedChildCategories =
                categoryRepository.findAllChildCategoriesByParentCategoryId(parentCategory.getId());

        assertThat(deletedChildCategories).isEmpty();
    }

    @Test
    void deleteCategory_DeletesCategory_CategoryWithChildCategories_ChildCategoriesWithProducts() {
        //arrange(setUp)
        createChildCategories();
        createProductsForChildCategories();

        //act
        categoryRepository.delete(parentCategory);

        //assert
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(parentCategoryName);

        assertThat(deletedCategory).isEmpty();

        Set<Category> deletedCategories =
                categoryRepository.findAllChildCategoriesByParentCategoryId(parentCategory.getId());

        Set<Product> deletedProductsFirstChildCategory =
                productRepository.findProductsByCategoryId(parentCategory.getChildCategories().stream().toList().get(0).getId());

        assertThat(deletedProductsFirstChildCategory).isEmpty();

        Set<Product> deletedProductsSecondChildCategory =
                productRepository.findProductsByCategoryId(parentCategory.getChildCategories().stream().toList().get(0).getId());

        assertThat(deletedProductsSecondChildCategory).isEmpty();

    }
}
