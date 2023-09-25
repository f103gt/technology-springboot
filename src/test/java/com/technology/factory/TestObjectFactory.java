package com.technology.factory;

import com.technology.category.models.Category;
import com.technology.product.models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

public class TestObjectFactory {
    public static Category createCategory(Integer id, String name, Category parent) {
        return Category.builder()
                .id(id)
                .categoryName(name)
                .parentCategory(parent)
                .childCategories(new HashSet<>())
                .products(new HashSet<>())
                .build();
    }
    //TODO make a separate TestCategoryFactory
}
