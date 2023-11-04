package com.technology.product.helpers;

import com.technology.product.dto.ProductDto;

public class ProductServiceHelper {
    public static int compareProductDtos(ProductDto productDto1, ProductDto productDto2) {
        /*int categoryComparison = productDto1.getCategoryName()
                .compareToIgnoreCase(productDto2.getCategoryName());
        if (categoryComparison == 0) {
            int quantityComparison = Integer.compare(
                    productDto1.getProductQuantity(), productDto2.getProductQuantity());
            if (quantityComparison == 0) {
                return productDto1.getProductName()
                        .compareToIgnoreCase(productDto2.getProductName());
            }
            return quantityComparison;
        }
        return categoryComparison;
    }*/

        return 0;
    }
}
