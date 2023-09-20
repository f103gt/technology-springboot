package com.technology.product.services;

import com.technology.category.models.Category;
import com.technology.product.ProductDto;
import com.technology.product.models.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ProductServiceImplGetAllProductsTest extends ProductServiceImplTest {

    @Test
    void getAllProducts() {
        //arrange
        Category category = Category.builder()
                .categoryName("Test Category")
                .build();
        List<Product> products = List.of(
                Product.builder()
                        .id(BigInteger.ONE)
                        .category(category)
                        .productName("Test Product 1")
                        .sku("SKU1")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build(),
                Product.builder()
                        .id(BigInteger.TWO)
                        .category(category)
                        .productName("Test Product 2")
                        .sku("SKU2")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build(),
                Product.builder()
                        .id(BigInteger.TEN)
                        .category(Category.builder()
                                .categoryName("Test Category 1")
                                .build())
                        .productName("Test Product 3")
                        .sku("SKU10")
                        .quantity(1)
                        .price(BigDecimal.TEN)
                        .build()

        );


        //act
        when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> productDtos = productService.getAllProducts();

        //assert
        IntStream.range(0, products.size())
                .forEach(index -> {
                    Product product = products.get(index);
                    ProductDto productDto = productDtos.get(index);
                    assertThat(productDto)
                            .extracting(ProductDto::getCategoryName, ProductDto::getProductName)
                            .containsExactly(product.getCategory().getCategoryName(), product.getProductName());
                });
    }
}
