package com.technology.product.registration.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NotNull
@NotEmpty
@NotBlank
@AllArgsConstructor
public class ProductRegistrationRequest {
    private String categoryName;
    private String productName;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
}
