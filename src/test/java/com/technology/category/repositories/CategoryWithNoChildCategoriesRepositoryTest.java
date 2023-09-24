package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.factory.TestObjectFactory;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWithNoChildCategoriesRepositoryTest extends CategoryRepositoryTest {
    @Autowired
    public CategoryWithNoChildCategoriesRepositoryTest(CategoryRepository categoryRepository, ProductRepository productRepository) {
        super(categoryRepository, productRepository);
    }

    @Test
    @DirtiesContext
    void deleteCategory_DeletesCategory_CategoryWithNoChildCategories() {
        //given super.setUp()

        Product product = TestObjectFactory.createProduct(
                BigInteger.ONE, parentCategory, "Test Product 1", "SKU1", 1, BigDecimal.TEN);
        createConnectionCategoryProduct(parentCategory,product);

        //when
        categoryRepository.delete(parentCategory);

        //then
        Optional<Category> deletedCategory =
                categoryRepository.findCategoryByCategoryName(parentCategory.getCategoryName());

        assertThat(deletedCategory).isEmpty();
        Set<Product> deletedProducts =
                productRepository.findProductsByCategoryId(parentCategory.getId());
        assertThat(deletedProducts).isEmpty();
    }
}
