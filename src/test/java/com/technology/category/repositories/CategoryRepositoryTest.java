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
import java.util.Optional;
import java.util.Set;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {
    protected final CategoryRepository categoryRepository;
    protected final ProductRepository productRepository;
    protected Category parentCategory;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository categoryRepository,
                                  ProductRepository productRepository) {
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

    protected void createProductsForChildCategories(int productsNumber, Category category) {
        Optional<BigInteger> maxIndex = category.getProducts().stream()
                .map(Product::getId)
                .max(BigInteger::compareTo);
        BigInteger index = BigInteger.ONE;
        if (maxIndex.isPresent()) {
            index = maxIndex.get().add(BigInteger.ONE);
        }
        while (index.compareTo(BigInteger.valueOf(productsNumber)) <= 0) {
            category.getProducts().add(
                    TestObjectFactory.createProduct(
                            index, category, "Test Product " + index.intValue(), "SKU" + index.intValue(), 1, BigDecimal.TEN));
            index = index.add(BigInteger.ONE);
            categoryRepository.save(category);
        }
        /*Product productSecond = TestObjectFactory.createProduct(BigInteger.TWO, childCategorySecond,
                "Test Product 2", "SKU2", 1, BigDecimal.TEN);
        createConnectionCategoryProduct(childCategorySecond, productSecond);*/
    }

    //creates a random number of products within a range from 1-5
    //for all the child categories of a parent category
    //TODO consider if i need this functionality
   /* protected  void createProductsForChildCategories(Category category){

    }*/
    protected void createConnectionCategoryProduct(Category category, Product product) {
        category.getProducts().add(product);
        categoryRepository.save(category);
    }

    private void createConnectionCategoryChildCategory(Category category, Category childCategory) {
        category.getChildCategories().add(childCategory);
        categoryRepository.save(category);
    }

}