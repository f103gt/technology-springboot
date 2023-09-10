package com.technology.category.registration.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRegistrationRequest {
    private String parentCategoryName;
    @NotBlank
    @NotEmpty
    @NotNull
    private String categoryName;
}
