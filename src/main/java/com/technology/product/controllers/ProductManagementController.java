package com.technology.product.controllers;

import com.technology.product.ProductDto;
import com.technology.product.registration.request.ProductRegistrationRequest;
import com.technology.product.services.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductManagementController {

    private final ProductService productService;

    public ProductManagementController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/manager/all-products")
    public ResponseEntity<List<ProductDto>> allProducts() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getAllProducts());
    }

    //TODO during the process of adding new product pass the product name to
    //TODO the add-characteristic controller in order to find out later on
    //TODO the product id by product name-
    @PostMapping("/manager/add-product")
    public ResponseEntity<String> addProduct(@RequestBody ProductRegistrationRequest productRegistrationRequest) {
        productService.saveProduct(productRegistrationRequest);
        return ResponseEntity.ok("The product was successfully added");
    }

    @DeleteMapping ("/manager/delete-product")
    public ResponseEntity<String> deleteProduct() {
        return ResponseEntity.ok("The product was successfully deleted");
    }
}
