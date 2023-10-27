package com.technology.product.services;

import com.technology.product.dto.GeneralProductDto;
import com.technology.product.dto.ProductDto;
import com.technology.product.registration.request.ProductRegistrationRequest;

import java.util.List;

public interface ProductService {
    void saveProduct(ProductRegistrationRequest productRegistrationRequest);

    void deleteProduct(String productName);
    ProductDto getProduct(String productName);

    List<ProductDto> getAllProducts();

    List<GeneralProductDto> getAllProductsByCagoryName(String categoryName);
}
