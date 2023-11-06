package com.technology.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technology.product.dto.GeneralProductDto;
import com.technology.product.dto.ProductDto;
import com.technology.product.registration.request.ProductRegistrationRequest;
import com.technology.product.services.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        List<GeneralProductDto> products=productService.getAllProductsByCagoryName(categoryName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(products);
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
    public ResponseEntity<String> addProduct(@RequestPart("product") String productJson,
                                             @RequestPart("description") MultipartFile description,
                                             @RequestPart("primaryImage") MultipartFile primaryImage,
                                             @RequestPart("images") List<MultipartFile> images) throws IOException {
        ProductRegistrationRequest product = new ObjectMapper().readValue(productJson, ProductRegistrationRequest.class);
        product.setDescription(description);
        product.setPrimaryImage(primaryImage);
        product.setImages(images);
        productService.saveProduct(product);
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
