package com.technology.product.controllers;

import com.technology.product.dto.GeneralProductDto;
import com.technology.product.dto.ProductDto;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;
import com.technology.product.services.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductManagementController {

    private final ProductService productService;

    public ProductManagementController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/api/v1/specific-product")
    public ResponseEntity<ProductDto> getSpecificProduct(
            @RequestParam("productName") String productName){
        ProductDto productDto = productService.getProduct(productName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productDto);
    }

    @GetMapping("api/v1/category-products")
    public ResponseEntity<List<GeneralProductDto>> allProductByCategory(
            @RequestParam("categoryName") String categoryName) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getAllProductsByCagoryName(categoryName));
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
    public ResponseEntity<String> addProduct(@RequestParam("categoryName") String categoryName,
                                             @RequestParam("productName") String productName,
                                             @RequestParam("sku") String sku,
                                             @RequestParam("quantity") int quantity,
                                             @RequestParam("price") BigDecimal price,
                                             @RequestParam("description") MultipartFile description,
                                             @RequestParam("primaryImage") MultipartFile primaryImage,
                                             @RequestParam("images") List<MultipartFile> images) {
        return ResponseEntity.ok("The product was successfully added");
    }

    /*@RequestPart("product") ProductRegistrationRequest product,
    @RequestPart("description") MultipartFile description,
    @RequestPart("primaryImage") MultipartFile primaryImage,
    @RequestPart("images") List<MultipartFile> images*/

    @DeleteMapping("/manager/delete-product")
    public ResponseEntity<String> deleteProduct() {
        return ResponseEntity.ok("The product was successfully deleted");
    }
}
