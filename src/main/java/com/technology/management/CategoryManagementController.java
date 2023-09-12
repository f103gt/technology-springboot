package com.technology.management;

import com.technology.category.dto.CategoryDto;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.services.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class CategoryManagementController {
    private final CategoryService categoryService;

    public CategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping ("/manager/all-categories")
    public ResponseEntity<List<CategoryDto>> allCategories() {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryService.getAllCategories());
    }

    @PostMapping ("/manager/add-category")
    public ResponseEntity<String> addCategory(@RequestBody CategoryRegistrationRequest categoryRegistrationRequest) {
        categoryService.saveCategory(categoryRegistrationRequest);
        return ResponseEntity.ok("A new category was successfully added");
    }

    @DeleteMapping ("/manager/delete-category")
    public ResponseEntity<String> deleteCategory(@RequestBody String categoryName) {
        categoryService.deleteCategory(categoryName);
        return ResponseEntity.ok("The category was successfully deleted");
    }
}
