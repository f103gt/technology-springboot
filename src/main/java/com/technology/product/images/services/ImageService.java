package com.technology.product.images.services;

import com.technology.product.models.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    void saveImage(MultipartFile multipartFile, Product product, boolean isPrimary) throws IOException;
}
