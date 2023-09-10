package com.technology.management;

import com.technology.products.registration.request.ProductRegistrationRequest;
import com.technology.products.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductManagementController {

    private final ProductService productService;

    public ProductManagementController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/manager/all-products")
    public ResponseEntity<String> allProducts() {
        return ResponseEntity.ok("A new category was successfully added");
    }

    //TODO during the process of adding new product pass the product name to
    //TODO the add-characteristic controller in order to find out later on
    //TODO the product id by product name-
    @PostMapping("/manager/add-product")
    public ResponseEntity<String> addProduct(@RequestBody ProductRegistrationRequest productRegistrationRequest) {
        productService.saveProduct(productRegistrationRequest);
        return ResponseEntity.ok("The product was successfully added");
    }

    @PostMapping("/manager/delete-product")
    public ResponseEntity<String> deleteProduct() {
        return ResponseEntity.ok("The product was successfully deleted");
    }
}
