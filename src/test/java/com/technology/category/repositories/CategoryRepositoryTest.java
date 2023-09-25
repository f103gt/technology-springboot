package com.technology.category.repositories;

import com.technology.category.models.Category;
import com.technology.category.test.repositories.TestProductRepository;
import com.technology.factory.TestObjectFactory;
import com.technology.factory.TestProductFactory;
import com.technology.product.models.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {
    protected final CategoryRepository categoryRepository;
    protected final TestProductRepository productRepository;
    protected Category parentCategory;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository categoryRepository,
                                  TestProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @BeforeEach
    void setUp() {
        parentCategory = TestObjectFactory.createCategory(1, "Parent Category", null);
        categoryRepository.save(parentCategory);
    }

    protected void createChildCategories(int categoriesNumber) {
        int index = 1;
        while (index <= categoriesNumber) {
            parentCategory.getChildCategories().add(
                    TestObjectFactory.createCategory(
                            index + 1, "Child Category " + index, parentCategory));
            categoryRepository.save(parentCategory);
            index++;
        }
    }

    protected void createProductsForCategory(int productsNumber, Category category) {
        Optional<BigInteger> maxIndex = productRepository.findMaxProductId();
        BigInteger index = BigInteger.ONE;
        if (maxIndex.isPresent()) {
            index = maxIndex.get().add(BigInteger.ONE);
        }
        while (index.compareTo(BigInteger.valueOf(productsNumber)) <= 0) {
            category.getProducts().add(
                    TestProductFactory.createProduct(
                            index, category, "Test Product " + index,
                            "SKU" + index, 1, BigDecimal.TEN));
            index = index.add(BigInteger.ONE);
            categoryRepository.save(category);
        }
    }

}