package com.technology.category.repositories;

import com.technology.category.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Optional<Category> findCategoryByCategoryName(String categoryName);
    void deleteCategoryByCategoryName(String categoryName);
}
