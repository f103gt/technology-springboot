package com.technology.product.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.product.dto.ProductDto;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.exceptions.ProductObjectAlreadyExistsException;
import com.technology.product.factories.ProductFactory;
import com.technology.product.helpers.ProductServiceHelper;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void saveProduct(ProductRegistrationRequest request) {
        String categoryName = request.getCategoryName().trim();
        String productName = request.getProductName().trim();
        Category category = categoryRepository
                .findCategoryByCategoryName(categoryName)
                .orElseThrow(
                        () -> new CategoryNotFoundException("Category " + categoryName + " not found."));
        if (productRepository.findProductByProductName(productName).isPresent()) {
            throw new ProductObjectAlreadyExistsException("Product with name "
                    + productName + " already exists.");
        }
        productRepository.save(ProductFactory.createProduct(category, request));
    }

    @Override
    public void deleteProduct(String productName) {
        if (productRepository.findProductByProductName(productName).isEmpty()) {
            throw new ProductNotFoundException("Product " + productName + " not found");
        }
        productRepository.deleteProductByProductName(productName);
    }

    //return products sorted by product category, product name,product quantity
    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductFactory::createProductDto)
                .sorted(ProductServiceHelper::compareProductDtos)
                .collect(Collectors.toList());
    }
}
