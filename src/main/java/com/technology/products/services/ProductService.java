package com.technology.products.services;

import com.technology.products.models.products.Product;
import com.technology.products.registration.request.ProductRegistrationRequest;

import java.util.Set;

public interface ProductService {
    void saveProduct(ProductRegistrationRequest productRegistrationRequest);
}
