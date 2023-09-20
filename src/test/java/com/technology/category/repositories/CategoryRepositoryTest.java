package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.factory.TestObjectFactory;
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
        parentCategory = TestObjectFactory.createCategory(1, parentCategoryName, null);
        categoryRepository.save(parentCategory);
    }

    protected void createChildCategories() {
        childCategoryFirst = TestObjectFactory.createCategory(2, "Child Category 1", parentCategory);
        childCategorySecond = TestObjectFactory.createCategory(3, "Child Category 2", parentCategory);

        categoryRepository.save(childCategoryFirst);
        categoryRepository.save(childCategorySecond);

        parentCategory.setChildCategories(Set.of(childCategoryFirst, childCategorySecond));
        categoryRepository.save(parentCategory);
    }

    protected void createProductsForChildCategories() {
       Product productFirst = TestObjectFactory.createProduct(BigInteger.ONE, childCategoryFirst,
                "Test Product 1", "SKU1", 1, BigDecimal.TEN);
        Product productSecond = TestObjectFactory.createProduct(BigInteger.TWO, childCategorySecond,
                "Test Product 2", "SKU2", 1, BigDecimal.TEN);

        childCategoryFirst.setProducts(Set.of(productFirst));
        categoryRepository.save(childCategoryFirst);

        productRepository.save(productSecond);

        childCategorySecond.setProducts(Set.of(productSecond));
        categoryRepository.save(childCategorySecond);
    }
}