package com.technology.product.services;

import com.technology.aws.AWSService;
import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.product.dto.GeneralProductDto;
import com.technology.product.dto.ProductDto;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.exceptions.ProductObjectAlreadyExistsException;
import com.technology.product.factories.ProductFactory;
import com.technology.product.helpers.ProductServiceHelper;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AWSService awsService;

    @Override
    @Transactional
    public ProductDto getProduct(String productName) {
        Product product = productRepository.findProductByProductName(productName)
                .orElseThrow(() ->
                        new ProductNotFoundException(productName+ " not found"));
        return ProductDto.builder()
                .productName(product.getProductName())
                .categoryName(product.getCategory().getCategoryName())
                .productPrice(product.getPrice())
                .productSku(product.getSku())
                .productQuantity(product.getQuantity())
                .build();
    }

    @Override
    @Transactional
    public List<GeneralProductDto> getAllProductsByCagoryName(String categoryName) {
        List<Product> products = productRepository.findProductsByCategoryCategoryName(categoryName.trim());
        return products.stream()
                .map(product -> GeneralProductDto.builder()
                        .productName(product.getProductName())
                        .productPrice(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveProduct(ProductRegistrationRequest request) throws IOException {
        String categoryName = request.getCategoryName().trim();
        String productName = request.getProductName().trim();
        Category category = categoryRepository
                .findCategoryByCategoryName(categoryName)
                .orElseThrow(
                        () -> new CategoryNotFoundException("Category " + categoryName + " not found."));
        if (productRepository.findProductByProductName(productName).isPresent()) {
            throw new ProductObjectAlreadyExistsException("Product with name "
                    + productName + " already exists.");
        }
        //productRepository.save(ProductFactory.createProduct(category, request));
        Product product = Product.builder()
                .category(category)
                .productName(request.getProductName().trim())
                .sku(request.getSku().trim())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .descriptionUrl(awsService.uploadFile(request.getDescription()))
                .primaryImageUrl(awsService.uploadFile(request.getPrimaryImage()))
                .imageUrls(request.getImages().stream()
                        .map(awsService::uploadFile)
                        .collect(Collectors.toList()))
                .build();
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(String productName) {
        if (productRepository.findProductByProductName(productName).isEmpty()) {
            throw new ProductNotFoundException("Product " + productName + " not found");
        }
        productRepository.deleteProductByProductName(productName);
    }

    //return products sorted by product category, product name,product quantity
    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductFactory::createProductDto)
                .sorted(ProductServiceHelper::compareProductDtos)
                .collect(Collectors.toList());
    }
}
