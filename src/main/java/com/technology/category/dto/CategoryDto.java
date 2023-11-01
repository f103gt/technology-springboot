package com.technology.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto {
    @JsonIgnore
    private String parentCategoryName;
    private String categoryName;
}

