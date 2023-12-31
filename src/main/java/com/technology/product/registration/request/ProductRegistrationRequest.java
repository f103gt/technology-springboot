package com.technology.product.registration.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NotNull
@NotEmpty
@NotBlank
@AllArgsConstructor
@NoArgsConstructor
public class ProductRegistrationRequest {
    private String categoryName;
    private String productName;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
    private MultipartFile description;
    private MultipartFile primaryImage;
    private List<MultipartFile> images;
}
