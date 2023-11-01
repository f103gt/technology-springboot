package com.technology.product.images.services;


import com.technology.product.images.models.Image;
import com.technology.product.images.repositories.ImageRepository;
import com.technology.product.models.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public void saveImage(MultipartFile multipartFile, Product product, boolean isPrimary) throws IOException {
        Image image = Image.builder()
                .product(product)
                .primaryImage(isPrimary)
                .imageData(multipartFile.getBytes())
                .build();
        imageRepository.save(image);
    }
}
