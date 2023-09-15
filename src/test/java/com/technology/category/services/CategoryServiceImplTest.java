package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;

    private CategoryServiceImpl categoryService;


    @BeforeEach
    void init() throws Exception {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void saveCategory_SavesCategory_WhenCategoryIsValidAndHasNoParentCategory() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setParentCategoryName(null);
        request.setCategoryName("TestCategory");
        //act
        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn(Optional.empty());
        categoryService.saveCategory(request);

        //assert
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void saveCategory_SavesCategory_WhenCategoryIsValidAndHasParentCategory() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setParentCategoryName("ParentTestCategory");
        request.setCategoryName("TestCategory");
        //act
        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn(Optional.empty());
        when(categoryRepository
                .findCategoryByCategoryName(request.getParentCategoryName()))
                .thenReturn(Optional.of(new Category()));

        categoryService.saveCategory(request);

        //assert
        verify(categoryRepository, times(1)).save(any(Category.class));
    }


    @Test
    void saveCategory_Throws_CategoryAlreadyExistsException() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setCategoryName("TestCategory");

        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn((Optional.of(new Category())));
        //act and assert
        assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.saveCategory(request),
                "Category " + request.getCategoryName() + " already exists");
    }

    @Test
    void saveCategory_Throws_ParentCategoryNotFoundException() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setParentCategoryName("NotFoundParentCategory");
        request.setCategoryName("TestCategory");

        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn((Optional.empty()));
        when(categoryRepository
                .findCategoryByCategoryName(request.getParentCategoryName()))
                .thenReturn(Optional.empty());
        //act and assert
        assertThrows(ParentCategoryNotFoundException.class,
                () -> categoryService.saveCategory(request),
                "Parent category " + request.getParentCategoryName() +
                        " not found. Check it for misspelling or try creating the mentioned parent category " +
                        "before adding a child one");
    }


    @Test
    void deleteCategory_DeletesCategory_IfCategoryPresent() {
        //arrange
        String categoryName = "TestCategory";
        Category category = new Category();
        category.setCategoryName(categoryName);

        //act
        when(categoryRepository
                .findCategoryByCategoryName(categoryName))
                .thenReturn(Optional.of(category));
        categoryService.deleteCategory(categoryName);

        //assert
        verify(categoryRepository, times(1)).deleteCategoryByCategoryName(categoryName);
    }

    @Test
    void deleteCategory_Throws_CategoryNotFoundException() {
        //arrange
        String categoryName = "TestCategory";
        Category category = new Category();
        category.setCategoryName(categoryName);

        //act
        when(categoryRepository
                .findCategoryByCategoryName(categoryName))
                .thenReturn(Optional.empty());
        //assert
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(categoryName),
                "Category " + categoryName + " not found.");
    }

    @Test
    void getAllCategories_Gets_AllCategoriesSorted() {
        //arrange
        Category parentCategory = new Category();
        parentCategory.setCategoryName("Parent Category");

        Category childCategoryFirst = new Category();
        childCategoryFirst.setCategoryName("Child Category 1");
        childCategoryFirst.setParentCategory(parentCategory);

        Category childCategorySecond = new Category();
        childCategorySecond.setCategoryName("Child Category 2");
        childCategorySecond.setParentCategory(parentCategory);

        //act
        when(categoryRepository.findAll())
                .thenReturn(
                        Arrays.asList(parentCategory,
                                childCategoryFirst,
                                childCategorySecond));

        List<CategoryDto> categories = categoryService.getAllCategories();
        //assert
        assertThat(categories).hasSize(3);
        assertThat(categories.get(0).getParentCategoryName()).isEqualTo("");
        CategoryDto categoryDtoFirst = categories.get(1);
        assertThat(categoryDtoFirst)
                .extracting(CategoryDto::getParentCategoryName,
                        CategoryDto::getCategoryName)
                .containsExactly("Parent Category","Child Category 1");
        CategoryDto categoryDtoSecond = categories.get(2);
        assertThat(categoryDtoSecond)
                .extracting(CategoryDto::getParentCategoryName,
                        CategoryDto::getCategoryName)
                .containsExactly("Parent Category","Child Category 2");
    }
}