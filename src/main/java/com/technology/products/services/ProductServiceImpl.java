package com.technology.products.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.exception.general.exceptions.ObjectNotFoundException;
import com.technology.products.exceptions.ProductNotFoundException;
import com.technology.products.exceptions.ProductObjectAlreadyExistsException;
import com.technology.products.models.Product;
import com.technology.products.registration.request.ProductRegistrationRequest;
import com.technology.products.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
    @Transactional
    public void saveProduct(ProductRegistrationRequest productRegistrationRequest) {
        String categoryName = productRegistrationRequest.getCategoryName();
        String productName = productRegistrationRequest.getProductName();
        Optional<Category> categoryOptional = categoryRepository.findCategoryByCategoryName(categoryName);
        if (categoryOptional.isEmpty()) {
            throw new CategoryNotFoundException("Category " + categoryName + " not found.");
        }
        if (productRepository.findProductByProductName(productName).isPresent()) {
            throw new ProductObjectAlreadyExistsException("Product with name "
                    + productName + " already exists.");
        }
        productRepository.save(Product.builder()
                .category(categoryOptional.get())
                .productName(productRegistrationRequest.getProductName())
                .sku(productRegistrationRequest.getSku())
                .quantity(productRegistrationRequest.getQuantity())
                .price(productRegistrationRequest.getPrice())
                .build());
    }

    @Override
    @Transactional
    public void deleteProduct(String productName) {
        if(productRepository.findProductByProductName(productName).isEmpty()){
            throw new ProductNotFoundException("Product "+productName+" not found");
        }
        productRepository.deleteProductByProductName(productName);
    }
}
