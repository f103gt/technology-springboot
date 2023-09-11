package com.technology.products.services;

import com.technology.products.ProductDto;
import com.technology.products.models.Product;
import com.technology.products.registration.request.ProductRegistrationRequest;

import java.util.List;

public interface ProductService {
    void saveProduct(ProductRegistrationRequest productRegistrationRequest);

    void deleteProduct(String productName);

    List<ProductDto> getAllProducts();
}
