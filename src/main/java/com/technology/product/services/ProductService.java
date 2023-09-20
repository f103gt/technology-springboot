package com.technology.product.services;

import com.technology.product.ProductDto;
import com.technology.product.registration.request.ProductRegistrationRequest;

import java.util.List;

public interface ProductService {
    void saveProduct(ProductRegistrationRequest productRegistrationRequest);

    void deleteProduct(String productName);

    List<ProductDto> getAllProducts();
}
