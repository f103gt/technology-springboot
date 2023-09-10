package com.technology.management;

import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryManagementController {
    private final CategoryService categoryService;

    public CategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/manager/all-categories")
    public ResponseEntity<String> addProduct() {
        return ResponseEntity.ok("A new product was successfully added");
    }

    @GetMapping("/manager/add-category")
    public ResponseEntity<String> addCategory(@RequestBody CategoryRegistrationRequest categoryRegistrationRequest) {
        categoryService.saveCategory(categoryRegistrationRequest);
        return ResponseEntity.ok("A new category was successfully added");
    }

    @PostMapping("/manager/delete-category")
    public ResponseEntity<String> deleteProduct() {
        return ResponseEntity.ok("The product was successfully deleted");
    }
}
