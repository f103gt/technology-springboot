package com.technology.category.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
public class JsonCategoryDto {
    private String categoryName;
    private List<JsonCategoryDto> childCategories;
}
