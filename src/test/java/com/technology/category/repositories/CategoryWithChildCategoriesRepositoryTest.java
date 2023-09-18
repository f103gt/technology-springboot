package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.products.models.Product;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWithChildCategoriesRepositoryTest extends CategoryRepositoryTest {
    @Autowired
    public CategoryWithChildCategoriesRepositoryTest(CategoryRepository categoryRepository, ProductRepository productRepository) {
        super(categoryRepository, productRepository);
    }

    private Category childCategoryFirst;
    private Category childCategorySecond;

    @BeforeEach
    void setUp() {
        super.setUp();
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
    }

    @Test
    void deleteCategory_DeletesCategory_CategoryWithChildCategories_WithNoProducts() {

        //arrange(setUp)
        categoryRepository.save(parentCategory);

        categoryRepository.save(childCategoryFirst);

        categoryRepository.save(childCategorySecond);

        parentCategory.setChildCategories(Set.of(childCategoryFirst, childCategorySecond));
        categoryRepository.save(parentCategory);

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
        categoryRepository.save(parentCategory);

        categoryRepository.save(childCategoryFirst);

        categoryRepository.save(childCategorySecond);

        parentCategory.setChildCategories(Set.of(childCategoryFirst, childCategorySecond));
        categoryRepository.save(parentCategory);

        Product productFirst =
                Product.builder()
                        .id(BigInteger.ONE)
                        .category(childCategoryFirst)
                        .productName("Test Product 1")
                        .sku("SKU1")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build();
        productRepository.save(productFirst);
        childCategoryFirst.setProducts(Set.of(productFirst));
        categoryRepository.save(childCategoryFirst);

        Product productSecond =
                Product.builder()
                        .id(BigInteger.TWO)
                        .category(childCategorySecond)
                        .productName("Test Product 2")
                        .sku("SKU2")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build();
        productRepository.save(productSecond);
        childCategorySecond.setProducts(Set.of(productSecond));
        categoryRepository.save(childCategorySecond);

        //act
        categoryRepository.delete(parentCategory);

        //assert
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(parentCategoryName);

        assertThat(deletedCategory).isEmpty();

        Set<Category> deletedCategories =
                categoryRepository.findAllChildCategoriesByParentCategoryId(parentCategory.getId());

        Set<Product> deletedProductsFirstChildCategory =
                productRepository.findProductsByCategoryId(childCategoryFirst.getId());

        assertThat(deletedProductsFirstChildCategory).isEmpty();

        Set<Product> deletedProductsSecondChildCategory =
                productRepository.findProductsByCategoryId(childCategorySecond.getId());

        assertThat(deletedProductsSecondChildCategory).isEmpty();

    }
}
