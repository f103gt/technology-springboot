package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.factory.TestObjectFactory;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.math.BigInteger;

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
        createConnectionCategoryChildCategory(parentCategory,childCategoryFirst);

        childCategorySecond = TestObjectFactory.createCategory(3, "Child Category 2", parentCategory);
        createConnectionCategoryChildCategory(parentCategory,childCategorySecond);
    }

    protected void createProductsForChildCategories() {
        Product productFirst = TestObjectFactory.createProduct(BigInteger.ONE, childCategoryFirst,
                "Test Product 1", "SKU1", 1, BigDecimal.TEN);
        createConnectionCategoryProduct(childCategoryFirst, productFirst);

        Product productSecond = TestObjectFactory.createProduct(BigInteger.TWO, childCategorySecond,
                "Test Product 2", "SKU2", 1, BigDecimal.TEN);
        createConnectionCategoryProduct(childCategorySecond, productSecond);
    }

    private void createConnectionCategoryProduct(Category category, Product product) {
        productRepository.save(product);

        category.getProducts().add(product);
        categoryRepository.save(category);
    }

    private void createConnectionCategoryChildCategory(Category category, Category childCategory){
        categoryRepository.save(childCategory);

        category.getChildCategories().add(childCategory);
        categoryRepository.save(category);
    }

}