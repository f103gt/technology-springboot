    package com.technology.products.services;

    import com.technology.products.models.products.Product;
    import com.technology.products.repositories.ProductRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.Set;

    @Service
    public class ProductServiceImpl implements ProductService{

        private final ProductRepository productRepository;

        @Autowired
        public ProductServiceImpl(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @Override
        @Transactional
        public void saveProduct(Product product) {
            productRepository.save(product);
        }

        @Override
        @Transactional
        public void saveProducts(Set<Product> products) {
            productRepository.saveAll(products);
        }
    }
