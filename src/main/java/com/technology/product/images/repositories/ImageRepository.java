package com.technology.product.images.repositories;


import com.technology.product.images.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface ImageRepository extends JpaRepository<Image,Integer> {
    Image getImageById(BigInteger id);
}
