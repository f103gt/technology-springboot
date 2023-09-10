    package com.technology.products.services;

    import com.technology.category.exceptions.CategoryNotFoundException;
    import com.technology.category.repositories.CategoryRepository;
    import com.technology.products.exceptions.ProductAlreadyExistsException;
    import com.technology.products.models.products.Product;
    import com.technology.products.registration.request.ProductRegistrationRequest;
    import com.technology.products.repositories.ProductRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.Set;

    @Service
    public class ProductServiceImpl implements ProductService{

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;

        @Autowired
        public ProductServiceImpl(ProductRepository productRepository,
                                  CategoryRepository categoryRepository) {
            this.productRepository = productRepository;
            this.categoryRepository = categoryRepository;
        }

        @Override
        @Transactional
        public void saveProduct(ProductRegistrationRequest productRegistrationRequest) {
            String categoryName = productRegistrationRequest.getCategoryName();
            String productName = productRegistrationRequest.getProductName();
            if(categoryRepository.findCategoryByCategoryName(categoryName).isEmpty()){
                throw new CategoryNotFoundException("Category "+categoryName+ " not found.");
            }
            if(productRepository.findProductByProductName(productName).isPresent()){
                throw new ProductAlreadyExistsException("Product with name "
                +productName+" already exists.");
            }
        }

    }
