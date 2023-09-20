package com.technology.product.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.product.ProductDto;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.exceptions.ProductObjectAlreadyExistsException;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (productRepository.findProductByProductName(productName).isEmpty()) {
            throw new ProductNotFoundException("Product " + productName + " not found");
        }
        productRepository.deleteProductByProductName(productName);
    }

    //return products sorted by product category, product name,product quantitry
    @Override
    @Transactional
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDto(
                        product.getCategory().getCategoryName(),
                        product.getProductName(),
                        product.getSku(),
                        product.getQuantity(),
                        product.getPrice()
                ))
                .sorted((productDto1, productDto2) ->
                {
                    int categoryComparison = productDto1.getCategoryName()
                            .compareToIgnoreCase(productDto2.getCategoryName());
                    if (categoryComparison == 0) {
                        int quantityComparison = Integer.compare(
                                productDto1.getQuantity(), productDto2.getQuantity());
                        if (quantityComparison == 0) {
                            return productDto1.getProductName()
                                    .compareToIgnoreCase(productDto2.getProductName());
                        }
                        return quantityComparison;
                    }
                    return categoryComparison;
                })
                .collect(Collectors.toList());
    }
}
