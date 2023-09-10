package com.technology.products.services;

import com.technology.products.registration.request.ProductRegistrationRequest;

public interface ProductService {
    void saveProduct(ProductRegistrationRequest productRegistrationRequest);
}
